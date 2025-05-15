package top.javarem.mcp.weixin.infrastructure.adapter;

import com.google.common.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import top.javarem.mcp.weixin.domain.adapter.IWeiXiPort;
import top.javarem.mcp.weixin.domain.model.WeiXinNoticeFunctionRequest;
import top.javarem.mcp.weixin.domain.model.WeiXinNoticeFunctionResponse;
import top.javarem.mcp.weixin.infrastructure.gateway.IWeixinApiService;
import top.javarem.mcp.weixin.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import top.javarem.mcp.weixin.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import top.javarem.mcp.weixin.properties.WeiXinApiProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WeiXiPort implements IWeiXiPort {

    @Resource
    private WeiXinApiProperties properties;

    @Resource
    private IWeixinApiService weixinApiService;

    @Resource
    private Cache<String, String> weixinAccessToken;

    @Override
    public WeiXinNoticeFunctionResponse weixinNotice(WeiXinNoticeFunctionRequest request) throws IOException {
        // 1. 获取 accessToken
        String accessToken = weixinAccessToken.getIfPresent(properties.getAppid());
        if (null == accessToken) {
            Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", properties.getAppid(), properties.getAppsecret());
            WeixinTokenResponseDTO weixinTokenResponseDTO = call.execute().body();
            assert weixinTokenResponseDTO != null;
            accessToken = weixinTokenResponseDTO.getAccess_token();
            weixinAccessToken.put(properties.getAppid(), accessToken);
        }

        // 2. 发送模板消息
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.platform, request.getPlatform());
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.subject, request.getSubject());
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.description, request.getDescription());

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO(properties.getTouser(), properties.getTemplate_id());
        templateMessageDTO.setUrl(request.getJumpUrl());
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();

        WeiXinNoticeFunctionResponse weiXinNoticeFunctionResponse = new WeiXinNoticeFunctionResponse();
        weiXinNoticeFunctionResponse.setSuccess(true);

        return weiXinNoticeFunctionResponse;
    }

}
