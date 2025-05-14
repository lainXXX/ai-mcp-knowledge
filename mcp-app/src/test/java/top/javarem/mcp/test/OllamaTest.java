package top.javarem.mcp.test;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import top.javarem.mcp.test.Utils.TokenTextSplitterWithContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @Author: rem
 * @Date: 2025/05/14/9:59
 * @Description:
 */
@Slf4j
@SpringBootTest
public class OllamaTest {

    @Resource
    private OllamaChatModel ollamaChatModel;

    @Resource(name = "ollamaSimpleVectorStore")
    private SimpleVectorStore simpleVectorStore;

    @Resource(name = "ollamaPgVectorStore")
    private PgVectorStore pgVectorStore;

    @Resource
    private TokenTextSplitter tokenTextSplitter;

    @Value("classpath:data/dog.png")
    private org.springframework.core.io.Resource imageResource;

    @Test
    public void test_call() {
        ChatResponse response = ollamaChatModel.call(new Prompt(
                "1+1",
                OllamaOptions.builder().model("deepseek-r1:1.5b").build()
        ));
        log.info("测试结果(call) : {}", JSON.toJSONString(response));
    }

    @Test
    public void test_call_images() {
        // 构建请求信息
        Media media = new Media(MimeType.valueOf(MimeTypeUtils.IMAGE_PNG_VALUE),
                imageResource);
        UserMessage userMessage = new UserMessage("请描述这张图片的主要内容", media);
        ChatResponse response = ollamaChatModel.call(new Prompt(
                userMessage,
                OllamaOptions.builder().model("deepseek-r1:1.5b").build()
        ));
        log.info("测试结果(images):{}", JSON.toJSONString(response));
    }

    @Test
    public void test_stream() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<ChatResponse> stream = ollamaChatModel.stream(new Prompt(
                "1+1",
                OllamaOptions.builder().model("deepseek-r1:1:5b").build()
        ));

        stream.subscribe(chatResponse -> {
                    chatResponse.getResult().getOutput();
                    log.info("测试结果(stream) ： {}", JSON.toJSONString(chatResponse));
                },
                Throwable::printStackTrace,
                () -> {
                    countDownLatch.countDown();
                    log.info("测试结果(stream) : done!");
                }
        );
        countDownLatch.await();
    }

    @Test
    public void upload() {
        TikaDocumentReader reader = new TikaDocumentReader("./data/file.txt");
        List<Document> documents = reader.get();
        TokenTextSplitterWithContext splitter = new TokenTextSplitterWithContext(100, 20);
        List<Document> documentSplitterList = splitter.split(documents);

        // 只对将要存储的分割后文档添加元数据
        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "ai知识库"));

        // 存储到向量数据库
        pgVectorStore.accept(documentSplitterList);
        log.info("上传成功");

    }

    @Test
    public void chat() {
        String message = "人工智能学科始于哪一年";

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;
        SearchRequest request = SearchRequest.builder()
                .query(message)
                .topK(5)
                .filterExpression("knowledge == 'ai知识库'")
                .build();
        List<Document> documents = pgVectorStore.similaritySearch(request);
        String collect = documents.stream().map(Document::getText).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", collect));
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(ragMessage);
        messages.add(new UserMessage(message));
        ChatResponse response = ollamaChatModel.call(new Prompt(
                messages,
                OllamaOptions.builder().model("deepseek-r1:1:5b").build()
        ));
        log.info("测试结果:{}", JSON.toJSONString(response));
    }

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ToolCallbackProvider tools;

    @Test
    public void test_tool() {
        String userInput = "有哪些工具可以使用";
        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OllamaOptions.builder().model("deepseek-r1:1.5b").build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }

    @Test
    public void test() {
        String userInput = "获取电脑配置";
        userInput = "获取电脑配置 在 \u202AC:\\Users\\aaa\\Desktop\\ 文件夹下，创建 电脑.txt 把电脑配置写入 电脑.txt";

        var chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(OllamaOptions.builder().model("deepseek-r1:1.5b").build())
                .build();

        System.out.println("\n>>> QUESTION: " + userInput);
        System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
    }


}
