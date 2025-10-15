package com.hackathon.safenet.application.service;

import com.hackathon.safenet.domain.model.meteoalarm.MeteoAlarmItem;
import com.hackathon.safenet.domain.model.meteoalarm.MeteoAlarmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeteoAlarmService {

    private static final String METEO_ALARM_URL = "https://feeds.meteoalarm.org/feeds/meteoalarm-legacy-rss-germany";

    private final RestTemplate restTemplate;

    public MeteoAlarmResponse getMeteoAlarmData(String language) {
        try {
            log.info("Fetching MeteoAlarm data for language: {}", language);
            
            // Fetch RSS feed
            String rssContent = restTemplate.getForObject(METEO_ALARM_URL, String.class);
            
            if (rssContent == null) {
                throw new RuntimeException("Failed to fetch RSS content");
            }

            // Parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(rssContent.getBytes()));

            // Extract channel information
            Element channel = (Element) document.getElementsByTagName("channel").item(0);
            String title = getTextContent(channel, "title");
            String description = getTextContent(channel, "description");
            String link = getTextContent(channel, "link");
            String channelLanguage = getTextContent(channel, "language");
            String ttlStr = getTextContent(channel, "ttl");
            Integer ttl = ttlStr != null ? Integer.parseInt(ttlStr) : null;

            // Extract items
            NodeList itemNodes = document.getElementsByTagName("item");
            List<MeteoAlarmItem> items = new ArrayList<>();

            for (int i = 1; i < itemNodes.getLength(); i++) {
                Element itemElement = (Element) itemNodes.item(i);
                MeteoAlarmItem item = parseItem(itemElement, language);
                if (item != null) {
                    items.add(item);
                }
            }

            return MeteoAlarmResponse.builder()
                    .title(title)
                    .description(description)
                    .link(link)
                    .language(channelLanguage)
                    .ttl(ttl)
                    .items(items)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching MeteoAlarm data", e);
            throw new RuntimeException("Failed to fetch MeteoAlarm data", e);
        }
    }

    private MeteoAlarmItem parseItem(Element itemElement, String requestedLanguage) {
        try {
            String title = getTextContent(itemElement, "title");
            String description = getTextContent(itemElement, "description");
            String link = getTextContent(itemElement, "link");
            String guid = getTextContent(itemElement, "guid");
            String pubDateStr = getTextContent(itemElement, "pubDate");

            LocalDateTime pubDate = parsePubDate(pubDateStr);

            // Extract awareness level and type from description
            Integer awarenessLevel = extractAwarenessLevel(description);
            Integer awarenessType = extractAwarenessType(description);

            // Extract time information
            LocalDateTime validFrom = extractDateTime(description, "From:");
            LocalDateTime validUntil = extractDateTime(description, "Until:");

            // Extract language-specific description
            String languageSpecificDescription = extractLanguageSpecificDescription(description, requestedLanguage);

            return MeteoAlarmItem.builder()
                    .title(title)
                    .description(languageSpecificDescription != null ? languageSpecificDescription : description)
                    .link(link)
                    .guid(guid)
                    .pubDate(pubDate)
                    .region(title) // Using title as region for now
                    .awarenessLevel(awarenessLevel)
                    .awarenessType(awarenessType)
                    .validFrom(validFrom)
                    .validUntil(validUntil)
                    .language(requestedLanguage)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing item", e);
            return null;
        }
    }

    private String getTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    private Integer extractAwarenessLevel(String description) {
        if (description == null) return null;
        Pattern pattern = Pattern.compile("level:(\\d+)");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private Integer extractAwarenessType(String description) {
        if (description == null) return null;
        Pattern pattern = Pattern.compile("awt:(\\d+)");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private LocalDateTime extractDateTime(String description, String prefix) {
        if (description == null) return null;
        // Pattern accounts for potential HTML tags around the prefix
        Pattern pattern = Pattern.compile(prefix + "\\s*</b>\\s*<i>([^<]+)</i>");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            try {
                String dateStr = matcher.group(1).trim();
                return ZonedDateTime.parse(dateStr).toLocalDateTime();
            } catch (Exception e) {
                log.warn("Failed to parse date from description: {}", matcher.group(1), e);
            }
        } else {
            log.info("No match found for prefix: {}", prefix);
        }
        return null;
    }

    private LocalDateTime parsePubDate(String pubDateStr) {
        if (pubDateStr == null) return null;
        try {
            DateTimeFormatter rfc822Formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("EEE, dd MMM yy HH:mm:ss ")
                    .appendOffset("+HHMM", "+0000")
                    .toFormatter(Locale.ENGLISH);

            return ZonedDateTime.parse(pubDateStr.trim(), rfc822Formatter).toLocalDateTime();
        } catch (Exception e) {
            log.warn("Failed to parse pubDate: {}", pubDateStr, e);
        }
        return null;
    }

    private String extractLanguageSpecificDescription(String description, String language) {
        if (description == null) return null;
        
        String languageCode = language.equals("german") ? "de-DE" : "en";
        Pattern pattern = Pattern.compile(languageCode + "\\):\\s*([^\\n]+(?:\\n(?!\\w+\\([^)]+\\):)[^\\n]*)*)");
        Matcher matcher = pattern.matcher(description);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }
}