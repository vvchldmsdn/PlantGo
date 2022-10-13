package com.ssafy.plantgo.util;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ssafy.plantgo.model.entity.Plant;
import com.ssafy.plantgo.model.repository.PlantRepository;
import com.ssafy.plantgo.model.service.PhotocardService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;

@RequiredArgsConstructor
public class PlantApiUtil {


    private final PlantRepository plantRepository;
    private final PhotocardService photocardService;

    public String getTagValue(String tag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        result = nlList.item(0).getTextContent();

        return result;
    }

    // tag값의 정보를 가져오는 함수
    public String getTagValue(String tag, String childTag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        for (int i = 0; i < eElement.getElementsByTagName(childTag).getLength(); i++) {

            //result += nlList.item(i).getFirstChild().getTextContent() + " ";
            result += nlList.item(i).getChildNodes().item(0).getTextContent() + " ";
        }

        return result;
    }

    public void insertPlant(int page) {
        String key = "XtSlfCZCfiW9YL5G%2B7vvhiDxX%2FYlveCtW8QV5DiKwDhln8JOghjrhEDyIA1Fhap0iK9QtulL7f2TlAg6MuI5Eg%3D%3D";

        try {
            // parsing할 url 지정(API 키 포함해서)
            String url = "http://openapi.nature.go.kr/openapi/service/rest/PlantService/plntIlstrSearch?serviceKey=" + key + "&numOfRows=100&pageNo=" + page;

            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document doc = dBuilder.parse(url);

            // 제일 첫번째 태그
            doc.getDocumentElement().normalize();

            // 파싱할 tag
            NodeList nList = doc.getElementsByTagName("item");
            System.out.println(nList.getLength());
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;
                String s3ImgUrl = "";
                /** 공공데이터에있는 imgUrl로 이미지 변환후 S3에 업로드해서 그 주소값 가져오기 */

                if (getTagValue("imgUrl",eElement).equals("NONE")) {
                    s3ImgUrl = "NONE";
                } else {
                    URL imgUrl = new URL(getTagValue("imgUrl", eElement));
                    BufferedImage image = ImageIO.read(imgUrl);
                    ImageIO.write(image,"jpg",new File("C:\\Users\\SSAFY\\Desktop\\특화프로젝트\\image/"+getTagValue("plantGnrlNm",eElement)));
                    File file = new File("C:\\Users\\SSAFY\\Desktop\\특화프로젝트\\image/"+getTagValue("plantGnrlNm",eElement));
                    FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

                    try {
                        InputStream input = new FileInputStream(file);
                        OutputStream os = fileItem.getOutputStream();
                        IOUtils.copy(input, os);
                        // Or faster..
                        // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
                    } catch (IOException ex) {
                        // do something.
                    }

                    MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                    s3ImgUrl = photocardService.upload(multipartFile);
                }
                Plant plant = Plant.builder()
                        .plantId(Integer.parseInt(getTagValue("plantPilbkNo", eElement)))
                        .korName(getTagValue("plantGnrlNm", eElement))
                        .schName(getTagValue("plantSpecsScnm", eElement))
                        .korNameSn(getTagValue("notRcmmGnrlNm", eElement))
                        .schNameSn(getTagValue("snnmScnm", eElement))
                        .imgUrl(s3ImgUrl)
                        .build();

                if (!getTagValue("detailYn", eElement).equals("Y")) continue;
                //상세정보 받기
                url = "http://openapi.nature.go.kr/openapi/service/rest/PlantService/plntIlstrInfo?serviceKey=" + key + "&q1=" + plant.getPlantId();

                dbFactoty = DocumentBuilderFactory.newInstance();
                dBuilder = dbFactoty.newDocumentBuilder();
                doc = dBuilder.parse(url);

                // 제일 첫번째 태그
                doc.getDocumentElement().normalize();

                // 파싱할 tag
                NodeList nList2 = doc.getElementsByTagName("item");
                nNode = nList2.item(0);
                eElement = (Element) nNode;

                //plant주입
                plant.setBfofMthd(getTagValue("bfofMthod", eElement));
                plant.setBranchDesc(getTagValue("branchDesc", eElement));
                plant.setBrdMthd(getTagValue("brdMthdDesc", eElement));
                plant.setBugInfo(getTagValue("bugInfo", eElement));
                plant.setDstrb(getTagValue("dstrb", eElement));
                plant.setFarmDesc(getTagValue("farmSpftDesc", eElement));
                plant.setFlwrDesc(getTagValue("flwrDesc", eElement));
                plant.setFruitDesc(getTagValue("fritDesc", eElement));
                plant.setGemDesc(getTagValue("gemmaDesc", eElement));
                plant.setGrwEnvDesc(getTagValue("grwEvrntDesc", eElement));
                plant.setLeafDesc(getTagValue("leafDesc", eElement));
                plant.setOrigin(getTagValue("orplcNm", eElement));
                plant.setForeDstrb(getTagValue("osDstrb", eElement));
                plant.setPeltDesc(getTagValue("peltDesc", eElement));
                plant.setPrtcDesc(getTagValue("prtcPlnDesc", eElement));
                plant.setRamenDesc(getTagValue("ramentumDesc", eElement));
                plant.setRootDesc(getTagValue("rootDesc", eElement));
                plant.setShape(getTagValue("shpe", eElement));
                plant.setStemDesc(getTagValue("stemDesc", eElement));
                plant.setSize(getTagValue("sz", eElement));
                plant.setUseMthd(getTagValue("useMthdDesc", eElement));
                plant.setWoodDesc(getTagValue("woodDesc", eElement));

                plantRepository.save(plant);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
