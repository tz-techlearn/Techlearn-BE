package com.techzen.techlearn.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techzen.techlearn.dto.request.GithubResquestDTO;
import com.techzen.techlearn.dto.response.GithubResponseDTO;
import com.techzen.techlearn.dto.response.StructResponseAIDTO;
import com.techzen.techlearn.enums.ErrorCode;
import com.techzen.techlearn.exception.AppException;
import com.techzen.techlearn.mapper.GithubMapper;
import com.techzen.techlearn.repository.ReviewConfigRepository;
import com.techzen.techlearn.service.AIService;
import com.techzen.techlearn.service.GithubService;
import com.techzen.techlearn.service.StructResponseService;
import com.techzen.techlearn.service.SubmitionService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GithubServiceImpl implements GithubService {
    static final String githubToken = Dotenv.load().get("GITHUB_TOKEN");
    static final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}/contents/{path}";
    RestTemplate restTemplate;
    GithubMapper githubMapper;
    ReviewConfigRepository reviewConfigRepository;
    AIService AIService;
    SubmitionService submitionService;
    StructResponseServiceImpl structResponseService;

    @Override
    public String reviewResponse(GithubResquestDTO resquestDTO) throws Exception {
        var content = getContent(resquestDTO.getGithub_link());
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(content).replace("\"", "'");
        jsonString = jsonString.replace("{x}", "\\\"");
        var description = reviewConfigRepository.findByActive().getPromptStructure();
        List<StructResponseAIDTO> listStruct  = structResponseService.getAll();
        String request = description.replace("{exercise}",resquestDTO.getExerciseTitle()
                .replace("\"", "'")) + jsonString;
        int sizeLoop = listStruct.size();

        for (int index = 0; index < sizeLoop; index++) {
            if (listStruct.get(index).getType().equals("language") && listStruct.get(index).getIsActive()) {
                request = request.replace("{language}", listStruct.get(index).getContentStruct().trim());
            }

            if(listStruct.get(index).getType().equals("struct") && listStruct.get(index).getIsActive()){
                request = request.replace("{struct}"," "+ listStruct.get(index)
                        .getContentStruct().trim()  + ", {struct}");
            }
            if(index == sizeLoop-1){
                request = request.replace("{struct}", "");
            }

        }

        var response = AIService.callAPI(request.replace("\n","").trim());
        submitionService.addSubmit(resquestDTO.getGithub_link(), response,
                resquestDTO.getIdUser(), resquestDTO.getIdAssignment());
        return response;
    }

    private List<GithubResponseDTO> getContent(String linkGithub) {
        return getContentRecursive(linkGithub, new ArrayList<>());
    }

    private List<GithubResponseDTO> getContentRecursive(String linkGithub, List<GithubResponseDTO> githubDTOList) {

        if(githubDTOList.size()>20){
            throw new AppException(ErrorCode.exceed_the_number_of_files);
        }

        // neu giong thi kh can goi getlink
        String apiUrl = linkGithub.contains("api.github.com") ? linkGithub : getAPILink(linkGithub);

        String response = callAPIGithub(apiUrl);
        JSONArray jsonArray = new JSONArray(response);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject fileObject = jsonArray.getJSONObject(i);
            String fileUrl = fileObject.getString("url");
            String fileType = fileObject.getString("type");

            if (fileType.equals("file")) {
                String fileResponse = callAPIGithub(fileUrl);
                JSONObject fileDetail = new JSONObject(fileResponse);
                var object = githubMapper.toGithubDTO(fileDetail.toMap());
                String objectContent = object.getContent();
                objectContent = objectContent.replaceAll("[^A-Za-z0-9+/=]", "");
                objectContent = objectContent.replace("\n", "");

                byte[] decodedBytes = Base64.getDecoder().decode(objectContent);
                String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
                char text = '"';
                System.out.println(decodedString.replace(String.valueOf(text), "\\\""));
                object.setContent(decodedString.replace(String.valueOf(text), "{x}"));
                githubDTOList.add(object);

            } else if (fileType.equals("dir")) {
                // de quy
                getContentRecursive(fileUrl, githubDTOList);
            }
        }
        return githubDTOList;
    }

    private String getAPILink(String linkGithub) {
        if (!linkGithub.startsWith("https://github.com/")) {
            throw new AppException(ErrorCode.GITHUB_NOT_FOUND);
        }
        String[] urlParts = linkGithub.split("/");
        if (urlParts.length < 7) {
            throw new AppException(ErrorCode.GITHUB_NOT_FOUND);
        }
        String owner = urlParts[3];
        String repo = urlParts[4];
        String branch = urlParts[6];
        String path = String.join("/", Arrays.copyOfRange(urlParts, 7, urlParts.length));

        return GITHUB_API_URL.replace("{owner}", owner)
                .replace("{repo}", repo)
                .replace("{path}", path)
                .replace("{branch}", branch);
    }

    private String callAPIGithub(String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new AppException(ErrorCode.GITHUB_NOT_FOUND);
            } else {
                throw new AppException(ErrorCode.GITHUB_API_ERROR);
            }
        } catch (RestClientException e) {
            throw new AppException(ErrorCode.GITHUB_API_ERROR);
        }
    }
}
