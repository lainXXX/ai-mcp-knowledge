package top.javarem.mcp.server.csdn.test;


import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Call;
import retrofit2.Response;
import top.javarem.mcp.server.csdn.domain.model.ArticleFunctionRequest;
import top.javarem.mcp.server.csdn.domain.model.ArticleFunctionResponse;
import top.javarem.mcp.server.csdn.domain.service.CSDNArticleService;
import top.javarem.mcp.server.csdn.infrastructure.gateway.ICSDNService;
import top.javarem.mcp.server.csdn.infrastructure.gateway.dto.ArticleRequestDTO;
import top.javarem.mcp.server.csdn.infrastructure.gateway.dto.ArticleResponseDTO;
import top.javarem.mcp.server.csdn.types.utils.MarkdownConverter;

import java.io.IOException;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    private final Logger log = LoggerFactory.getLogger(ApiTest.class);

    @Autowired
    private ICSDNService csdnService;

    @Autowired
    private CSDNArticleService csdnArticleService;

    @Test
    public void test_saveArticle() throws IOException {
        // 1. 构建请求对象
        ArticleRequestDTO request = new ArticleRequestDTO();
        request.setTitle("测试文章标题01");
        request.setMarkdowncontent("# 测试文章内容\n这是一篇测试文章");
        request.setContent("<h1>测试文章内容</h1><p>这是一篇测试文章</p>");
        request.setReadType("public");
        request.setLevel("0");
        request.setTags("测试,文章");
        request.setStatus(0);
        request.setCategories("后端");
        request.setType("original");
        request.setOriginal_link("");
        request.setAuthorized_status(true);
        request.setDescription("这是一篇测试文章的描述");
        request.setResource_url("");
        request.setNot_auto_saved("0");
        request.setSource("pc_mdeditor");
        request.setCover_images(Collections.emptyList());
        request.setCover_type(0);
        request.setIs_new(1);
        request.setVote_id(0);
        request.setResource_id("");
        request.setPubStatus("draft");
        request.setSync_git_code(0);

        // 2. 调用接口
        String cookie = "dc_sid=e3cce4c923dafa0a9e8d11a9d6102c50; fid=20_72602451227-1747208643691-190344; uuid_tt_dd=11_89873883056-1747208643693-953086; dc_session_id=11_1747208643693.691474; c_first_ref=default; c_segment=14; SESSION=d671786a-451a-48f8-a30e-114154a63cda; creative_btn_mp=1; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1747208647; HMACCOUNT=328E28A5378A4D37; hide_login=1; is_advert=1; UserName=m0_75125940; UserInfo=a0ecfad0247146078fa91e28850b65f7; UserToken=a0ecfad0247146078fa91e28850b65f7; UserNick=m0_75125940; AU=55D; UN=m0_75125940; BT=1747208689470; p_uid=U010000; csdn_newcert_m0_75125940=1; creativeSetApiNew=%7B%22toolbarImg%22%3A%22https%3A//img-home.csdnimg.cn/images/20231011044944.png%22%2C%22publishSuccessImg%22%3A%22https%3A//img-home.csdnimg.cn/images/20240229024608.png%22%2C%22articleNum%22%3A0%2C%22type%22%3A0%2C%22oldUser%22%3Afalse%2C%22useSeven%22%3Atrue%2C%22oldFullVersion%22%3Afalse%2C%22userName%22%3A%22m0_75125940%22%7D; c_first_page=https%3A//editor.csdn.net/md/%3FarticleId%3D147956533; c_dsid=11_1747209419267.685576; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1747209512; c_pref=https%3A//mp.csdn.net/mp_blog/creation/success/147956533; c_ref=https%3A//editor.csdn.net/; c_page_id=default; log_Id_pv=15; log_Id_view=279; dc_tos=sw8rhw; log_Id_click=18";
        Call<ArticleResponseDTO> call = csdnService.saveArticle(request, cookie);
        Response<ArticleResponseDTO> response = call.execute();

        System.out.println("\r\n测试结果" + JSON.toJSONString(response));

        // 3. 验证结果
        if (response.isSuccessful()) {
            ArticleResponseDTO articleResponseDTO = response.body();
            log.info("发布文章成功 {}", articleResponseDTO);
        }
    }

    @Test
    public void test_md2html() {
        System.out.println(MarkdownConverter.convertToHtml("**关于DDD是什么，在维基百科有一个明确的定义。\"Domain-driven design (DDD) is a major software design approach.\" 也就是说DDD是一种主要的软件设计方法。而软件设计涵盖了；范式、模型、框架、方法论。**\n" +
                "\n" +
                "- 范式（paradigm）指的是一种编程思想。\n" +
                "- 模型（model）指的是对现实世界或者问题的抽象描述。\n" +
                "- 框架（framework）指的是提供了一系列通用功能和结构的软件工具。\n" +
                "- 方法论（methodology）指的是一种系统的、有组织的解决问题的方法。\n" +
                "\n" +
                "所以，DDD不只是只有指导思想，伴随的DDD的还包括框架结构分层。但说到底，这些仍然是理论讨论。在没有一个DDD落地项目物参考下，其实大部分码农是没法完成DDD开发的。所以小傅哥今年花费了5个月假期/周末的时间，完成的《DDD简明开发教程》，帮助大家落地DDD编码。"));
    }

    @Test
    public void test_domain_saveArticle() throws IOException {
        String json = "{\"content\":\"<h2>场景：</h2>\\n<p>在某互联网大厂的面试室，一位严肃的面试官正准备提问，而对面坐着一位看似紧张却又想显得轻松的程序员小张。</p>\\n<p><strong>面试官</strong>：我们先来聊聊Java核心知识。第一个问题，Java中的JVM是如何管理内存的？</p>\\n<p><strong>程序员小张</strong>：哦，这个简单！JVM就像一个巨大的购物车，负责把所有的变量都放进去，呃……然后就……管理起来？</p>\\n<p><strong>面试官</strong>：嗯，第二个问题，请说说HashMap的工作原理。</p>\\n<p><strong>程序员小张</strong>：HashMap嘛，就是……呃，一个很大的箱子，大家都往里面扔东西，有时候会打架……</p>\\n<p><strong>面试官</strong>：那么第三个问题，能不能讲讲Spring和SpringBoot的区别？</p>\\n<p><strong>程序员小张</strong>：Spring是……呃，春天？SpringBoot就是穿靴子的春天嘛！哈哈……</p>\\n<p><strong>面试官</strong>：好，今天的问题就问到这里。回去等通知吧。</p>\\n<h2>答案解析：</h2>\\n<ol>\\n<li>\\n<p><strong>JVM内存管理</strong>：JVM内存管理包括堆内存和栈内存，堆内存用于存储对象实例，栈内存用于执行线程时的栈帧。</p>\\n</li>\\n<li>\\n<p><strong>HashMap原理</strong>：HashMap通过哈希函数将键映射到对应的值，并通过链表解决哈希冲突。</p>\\n</li>\\n<li>\\n<p><strong>Spring与SpringBoot区别</strong>：Spring是一个大型应用框架，而SpringBoot是基于Spring的快速开发套件，简化了Spring应用的配置。</p>\\n</li>\\n</ol>\\n\",\"cover_images\":[],\"cover_type\":0,\"description\":\"在互联网大厂的面试中，严肃的面试官与搞笑的程序员上演了一场精彩的对话。面试官提出Java核心知识、HashMap、Spring等问题，程序员则用幽默的方式作答。本文不仅展现了轻松的面试氛围，还附上了详细的技术问题答案解析，帮助读者更好地理解相关知识。\",\"is_new\":1,\"level\":\"0\",\"markdowncontent\":\"## 场景：\\n\\n在某互联网大厂的面试室，一位严肃的面试官正准备提问，而对面坐着一位看似紧张却又想显得轻松的程序员小张。\\n\\n**面试官**：我们先来聊聊Java核心知识。第一个问题，Java中的JVM是如何管理内存的？\\n\\n**程序员小张**：哦，这个简单！JVM就像一个巨大的购物车，负责把所有的变量都放进去，呃……然后就……管理起来？\\n\\n**面试官**：嗯，第二个问题，请说说HashMap的工作原理。\\n\\n**程序员小张**：HashMap嘛，就是……呃，一个很大的箱子，大家都往里面扔东西，有时候会打架……\\n\\n**面试官**：那么第三个问题，能不能讲讲Spring和SpringBoot的区别？\\n\\n**程序员小张**：Spring是……呃，春天？SpringBoot就是穿靴子的春天嘛！哈哈……\\n\\n**面试官**：好，今天的问题就问到这里。回去等通知吧。\\n\\n## 答案解析：\\n\\n1. **JVM内存管理**：JVM内存管理包括堆内存和栈内存，堆内存用于存储对象实例，栈内存用于执行线程时的栈帧。\\n\\n2. **HashMap原理**：HashMap通过哈希函数将键映射到对应的值，并通过链表解决哈希冲突。\\n\\n3. **Spring与SpringBoot区别**：Spring是一个大型应用框架，而SpringBoot是基于Spring的快速开发套件，简化了Spring应用的配置。\",\"not_auto_saved\":\"0\",\"pubStatus\":\"draft\",\"readType\":\"public\",\"resource_id\":\"\",\"resource_url\":\"\",\"source\":\"pc_mdeditor\",\"status\":0,\"sync_git_code\":0,\"tags\":\"Java,面试,互联网,程序员,Spring,SpringBoot,HashMap,JVM\",\"title\":\"互联网大厂Java面试：严肃面试官与搞笑程序员的对决\",\"vote_id\":0}";
        ArticleFunctionRequest request = JSON.parseObject(json, ArticleFunctionRequest.class);
        ArticleFunctionResponse response = csdnArticleService.saveArticle(request);
        log.info("测试结果:{}", JSON.toJSONString(response));
    }

}
