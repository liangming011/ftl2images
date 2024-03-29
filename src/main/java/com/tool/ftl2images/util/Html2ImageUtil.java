package com.tool.ftl2images.util;

import cn.hutool.core.img.Img;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.util.CharsetUtil;
import org.springframework.util.Base64Utils;
import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liangming
 */
public class Html2ImageUtil {

    private Html2ImageUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String FTL_TEMPLATE = "Index.ftl";

    private static Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_31);

    }

    public static String freemarkerRender(Map<String, Object> data, String ftlHtml) throws IOException, TemplateException {
        Writer out = new StringWriter();
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate(FTL_TEMPLATE, ftlHtml);
        configuration.setTemplateLoader(stringTemplateLoader);
        Template template = configuration.getTemplate(FTL_TEMPLATE, CharsetUtil.UTF_8.name());
        template.process(data, out);
        out.flush();
        return out.toString();
    }

    public static String createImages(String html, int width) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(html.getBytes()));

        Java2DRenderer renderer = new Java2DRenderer(document, width);
        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setDotsPerPixel(2);
        sharedContext.setDPI(600);
        Map<RenderingHints.Key, Object> map = new HashMap<>(10);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        map.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        map.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        renderer.setRenderingHints(map);


        BufferedImage image = renderer.getImage();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // ImageIO.write(image, "png", stream);
        Img.from(image).setQuality(0.5).write(stream);

        // data:image/png;base64,{}
        return "data:image/png;base64," + Base64Utils.encodeToString(stream.toByteArray());
    }
}
