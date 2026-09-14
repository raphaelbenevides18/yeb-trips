package org.benevides.entity.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CotacaoResponse {

    @JsonProperty("BRLEUR")
    private CotacaoDetalhe brlEur;

    public CotacaoDetalhe getBrlEur() {
        return brlEur;
    }

    public void setBrlEur(CotacaoDetalhe brlEur) {
        this.brlEur = brlEur;
    }

    public static class CotacaoDetalhe {
        private String code;
        private String codein;
        private String bid; // Valor da cotação


        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getCodein() { return codein; }
        public void setCodein(String codein) { this.codein = codein; }

        public String getBid() { return bid; }
        public void setBid(String bid) { this.bid = bid; }
    }
}
