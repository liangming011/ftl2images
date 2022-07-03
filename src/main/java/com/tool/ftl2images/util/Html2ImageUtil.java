package com.tool.ftl2images.util;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.netty.util.CharsetUtil;
import org.springframework.util.Base64Utils;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Html2ImageUtil {

    private static Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setTemplateLoader(new URLTemplateLoader() {
            @Override
            protected URL getURL(String s) {
                try {
                    return new URL("http://localhost:8080/cache/ftl");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static String freemarkerRender(Map<String, Object> data, String ftlname) throws Exception {
        Writer out = new StringWriter();
        Template template = configuration.getTemplate(ftlname, CharsetUtil.UTF_8.name());
        template.process(data, out);
        out.flush();
        String html = out.toString();
        return html;
    }

    public static String createImages(String html, int width) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(html.getBytes()));

        Java2DRenderer renderer = new Java2DRenderer(document, width);
        BufferedImage image = renderer.getImage();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        // data:image/png;base64,{}
        return "data:image/png;base64,"+ Base64Utils.encodeToString(stream.toByteArray());
    }
}