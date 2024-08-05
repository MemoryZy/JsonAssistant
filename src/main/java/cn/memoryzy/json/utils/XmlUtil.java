package cn.memoryzy.json.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author Memory
 * @since 2024/8/5
 */
public class XmlUtil {

    public static boolean isXML(String text) {
        if (StrUtil.isBlank(text)) return false;

        try {
            cn.hutool.core.util.XmlUtil.parseXml(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String xmlToJson(String xml) {
        try {
            ObjectMapper xmlMapper = new XmlMapper();
            JsonNode jsonNode = xmlMapper.readTree(xml.getBytes());
            return JsonUtil.MAPPER.writeValueAsString(jsonNode);
        } catch (Exception e) {
            return null;
        }
    }

}
