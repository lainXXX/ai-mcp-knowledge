package top.javarem.mcp.weixin.domain.adapter;

import top.javarem.mcp.weixin.domain.model.WeiXinNoticeFunctionRequest;
import top.javarem.mcp.weixin.domain.model.WeiXinNoticeFunctionResponse;

import java.io.IOException;

public interface IWeiXiPort {
    WeiXinNoticeFunctionResponse weixinNotice(WeiXinNoticeFunctionRequest request) throws IOException;

}
