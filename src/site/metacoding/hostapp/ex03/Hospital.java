package site.metacoding.hostapp.ex03;

import lombok.Data;

// 컬럼 16개
@Data
class Hospital {
    private int id; // 프라이머리 키 -> 시퀀스 생성 seq_hospital
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
