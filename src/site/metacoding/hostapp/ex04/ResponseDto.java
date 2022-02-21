package site.metacoding.hostapp.ex04;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Body {
    private Items items;
    private Integer numOfRows;
    private Integer pageNo;
    private Integer totalCount;
}

@Data
@AllArgsConstructor
class Header {
    private String resultCode;
    private String resultMsg;
}

@Data
@AllArgsConstructor
class Item {
    private String addr;
    private Integer mgtStaDd;
    private String pcrPsblYn;
    private String ratPsblYn;
    private Integer recuClCd;
    private String rprtWorpClicFndtTgtYn;
    private String sgguCdNm;
    private String sidoCdNm;
    private String telno;
    private Integer xPos;
    private Double xPosWgs84;
    private Integer yPos;
    private Double yPosWgs84;
    private String yadmNm;
    private String ykihoEnc;
}

@Data
@AllArgsConstructor
class Items {
    private List<Item> item = null;
}

@Data
@AllArgsConstructor
class Response {
    private Header header;
    private Body body;
}

@Data
@AllArgsConstructor
public class ResponseDto {

    private Response response;
}
