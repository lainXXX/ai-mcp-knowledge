package top.javarem.mcp.weixin.domain.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import top.javarem.mcp.weixin.domain.adapter.IWeiXiPort;
import top.javarem.mcp.weixin.domain.model.WeiXinNoticeFunctionRequest;
import top.javarem.mcp.weixin.domain.model.WeiXinNoticeFunctionResponse;

import java.io.IOException;

/**
 * 微信通知服务接口
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-04-04 18:52
 */
@Slf4j
@Service
public class WeiXinNoticeService {

    @Resource
    private IWeiXiPort weiXiPort;

    @Tool(description = "微信公众号消息通知")
    public WeiXinNoticeFunctionResponse weixinNotice(WeiXinNoticeFunctionRequest request) throws IOException {
        log.info("微信消息通知，平台:{} 主题:{} 描述:{}", request.getPlatform(), request.getSubject(), request.getDescription());
        return weiXiPort.weixinNotice(request);
    }

}
