package com.github.paicoding.forum.service.image.oss;

import com.aliyun.imageaudit20191230.Client;
import com.aliyun.imageaudit20191230.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.github.paicoding.forum.core.config.ImageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@ConditionalOnExpression(value = "#{'ali'.equals(environment.getProperty('image.oss.type'))}")
@Component
public class ContextAudit implements InitializingBean {
    @Autowired
    private ImageProperties imageProperties;
    private static final String ENDPOINT = "imageaudit.cn-shanghai.aliyuncs.com";
    private static Client client;

    // 图片和文本审核内容
    private static final List<String> imageScene = Arrays.asList("porn", "terrorism", "live", "logo");// "ad"
    static List<String> textScene = Arrays.asList("spam", "politics", "abuse", "terrorism", "porn", "flood", "contraband");// "ad"

    @Override
    public void afterPropertiesSet() throws Exception {
        Config config = new Config().setAccessKeyId(imageProperties.getOss().getAk())
                .setAccessKeySecret(imageProperties.getOss().getSk())
                .setEndpoint(ENDPOINT);
        client = new Client(config);
    }

    /**
     * 检测图片内容是否违规
     *
     * @param image
     * @return
     * @throws Exception
     */
    public static String scanImage(String image) throws Exception {
        return Image.scanImage(image);
    }

    /**
     * 检测文本内容是否违规
     *
     * @param text
     * @return
     */
    public static String scanText(String text) {
        return Text.scanText(text);
    }

    static class Image {
        public static String scanImage(String image) throws Exception {
            ScanImageRequest.ScanImageRequestTask task0 = new ScanImageRequest.ScanImageRequestTask().setImageURL(image);
            ScanImageRequest scanImageRequest = new ScanImageRequest().setTask(Collections.singletonList(task0)).setScene(imageScene);

            RuntimeOptions runtime = new RuntimeOptions();
            ScanImageResponse response = client.scanImageWithOptions(scanImageRequest, runtime);

            List<ScanImageResponseBody.ScanImageResponseBodyDataResultsSubResults> responseSubResults = response.getBody().getData().getResults().get(0).getSubResults();

            for (ScanImageResponseBody.ScanImageResponseBodyDataResultsSubResults responseSubResult : responseSubResults) {
                if (!"pass".equals(responseSubResult.getSuggestion())) {
                    return generateErrorMessage(responseSubResult.getScene(), responseSubResult.getLabel(), responseSubResult.getSuggestion());
                }
            }
            return "";
        }


        private static String generateErrorMessage(String scene, String label, String suggestion) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Scene: ").append(scene).append(", Label: ").append(label).append(", Suggestion: ").append(suggestion).append(", ");
            errorMsg.append("Error Detail: ");
            switch (scene) {
                case "porn":
                    errorMsg.append(generatePornErrorMessage(label));
                    break;
                case "terrorism":
                    errorMsg.append(generateTerrorismErrorMessage(label));
                    break;
                case "ad":
                    errorMsg.append(generateAdErrorMessage(label));
                    break;
                case "live":
                    errorMsg.append(generateLiveErrorMessage(label));
                    break;
                case "logo":
                    errorMsg.append(generateLogoErrorMessage(label));
                    break;
            }
            return errorMsg.toString();
        }

        private static String generatePornErrorMessage(String label) {
            switch (label) {
                case "normal":
                    return "检测到正常图片。";
                case "sexy":
                    return "检测到性感图片。";
                case "porn":
                    return "检测到色情内容。";
                default:
                    return "未知错误。";
            }
        }

        private static String generateTerrorismErrorMessage(String label) {
            switch (label) {
                case "normal":
                    return "检测到正常图片。";
                case "bloody":
                    return "检测到血腥内容。";
                case "explosion":
                    return "检测到爆炸内容。";
                case "outfit":
                    return "检测到特殊装束内容。";
                case "logo":
                    return "检测到特殊标识内容。";
                case "weapon":
                    return "检测到武器内容。";
                case "politics":
                    return "检测到敏感内容。";
                case "violence":
                    return "检测到打斗内容。";
                case "crowd":
                    return "检测到聚众内容。";
                case "parade":
                    return "检测到游行内容。";
                case "carcrash":
                    return "检测到车祸现场内容。";
                case "flag":
                    return "检测到旗帜内容。";
                case "location":
                    return "检测到地标内容。";
                case "drug":
                    return "检测到涉毒内容。";
                case "gamble":
                    return "检测到赌博内容。";
                case "others":
                    return "检测到其他内容。";
                default:
                    return "未知错误。";
            }
        }

        private static String generateAdErrorMessage(String label) {
            switch (label) {
                case "normal":
                    return "检测到正常图片。";
                case "politics":
                    return "检测到包含敏感政治内容的图片。";
                case "abuse":
                    return "检测到包含辱骂内容的图片。";
                case "terrorism":
                    return "检测到包含恐怖内容的图片。";
                case "contraband":
                    return "检测到包含违禁内容的图片。";
                case "spam":
                    return "检测到包含垃圾内容的图片。";
                case "npx":
                    return "检测到包含牛皮癣广告的图片。";
                case "qrcode":
                    return "检测到包含二维码的图片。";
                case "programCode":
                    return "检测到包含小程序码的图片。";
                case "ad":
                    return "检测到其他广告内容的图片。";
                default:
                    return "未知错误。";
            }
        }

        private static String generateLiveErrorMessage(String label) {
            switch (label) {
                case "normal":
                    return "检测到正常图片。";
                case "meaningless":
                    return "检测到无意义图片。";
                case "PIP":
                    return "检测到画中画内容。";
                case "smoking":
                    return "检测到吸烟内容。";
                case "drivelive":
                    return "检测到车内直播内容。";
                case "drug":
                    return "检测到涉毒内容。";
                case "gamble":
                    return "检测到赌博内容。";
                default:
                    return "未知错误。";
            }
        }

        private static String generateLogoErrorMessage(String label) {
            switch (label) {
                case "normal":
                    return "检测到正常图片。";
                case "TV":
                    return "检测到包含电视台Logo的图片。";
                case "trademark":
                    return "检测到包含商标的图片。";
                default:
                    return "未知错误。";
            }
        }
    }

    static class Text {
        private static final List<ScanTextRequest.ScanTextRequestLabels> labels;

        static {
            labels = new ArrayList<>();
            for (String label : textScene) {// "ad"
                labels.add(new ScanTextRequest.ScanTextRequestLabels().setLabel(label));
            }
        }


        public static String scanText(String text) {
            ScanTextRequest.ScanTextRequestTasks tasks = new ScanTextRequest.ScanTextRequestTasks().setContent(text);

            ScanTextRequest scanTextRequest = new ScanTextRequest().setLabels(labels).setTasks(Collections.singletonList(tasks));
            RuntimeOptions runtime = new RuntimeOptions();
            ScanTextResponse response;
            try {
                response = client.scanTextWithOptions(scanTextRequest, runtime);
                ScanTextResponseBody.ScanTextResponseBodyDataElementsResults results = response.getBody().getData()
                        .getElements().get(0).getResults().get(0);

                if (!results.getSuggestion().equals("pass")) {
                    return generateErrorMessage(results);
                }
            } catch (Exception e) {
                log.error("Content Scanning Failure! {}", e.getMessage());
                return "";
            }

            return "";
        }

        private static String generateErrorMessage(ScanTextResponseBody.ScanTextResponseBodyDataElementsResults results) {
            StringBuilder errorMsg = new StringBuilder("Suggestion: " + results.getSuggestion() + ", Error Detail: ");

            List<ScanTextResponseBody.ScanTextResponseBodyDataElementsResultsDetails> details = results.getDetails();
            for (ScanTextResponseBody.ScanTextResponseBodyDataElementsResultsDetails detail : details) {
                switch (detail.getLabel()) {
                    case "spam":
                        errorMsg.append("文本包含垃圾内容。");
                        break;
                    case "politics":
                        errorMsg.append("文本包含敏感政治内容。");
                        break;
                    case "abuse":
                        errorMsg.append("文本包含辱骂内容。");
                        break;
                    case "terrorism":
                        errorMsg.append("文本包含恐怖主义内容。");
                        break;
                    case "porn":
                        errorMsg.append("文本包含色情内容。");
                        break;
                    case "flood":
                        errorMsg.append("文本包含灌水内容。");
                        break;
                    case "contraband":
                        errorMsg.append("文本包含违禁内容。");
                        break;
                    case "ad":
                        errorMsg.append("文本包含广告内容。");
                        break;
                    default:
                        errorMsg.append("文本包含未知的不适当内容。");
                        break;
                }
            }

            return errorMsg.toString();
        }
    }
}
