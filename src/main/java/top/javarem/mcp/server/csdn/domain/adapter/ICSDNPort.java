package top.javarem.mcp.server.csdn.domain.adapter;



import top.javarem.mcp.server.csdn.domain.model.ArticleFunctionRequest;
import top.javarem.mcp.server.csdn.domain.model.ArticleFunctionResponse;

import java.io.IOException;

public interface ICSDNPort {

    ArticleFunctionResponse writeArticle(ArticleFunctionRequest request) throws IOException;

}
