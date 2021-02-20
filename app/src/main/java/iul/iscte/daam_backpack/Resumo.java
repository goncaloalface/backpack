package iul.iscte.daam_backpack;

import android.graphics.Bitmap;

public class Resumo {

    private String Nome;
    private String Cadeira;
    private String Universidade;
    private int TotalFotos;
    private int Acessos = 0;
    private long DataPublicacao;
    private String UserId;

    public Resumo(){
    }

    public Resumo(String nome, String cadeira, String universidade, int totalFotos, String userid){
        this.Nome = nome;
        this.Cadeira = cadeira;
        this.Universidade = universidade;
        this.TotalFotos = totalFotos;
        this.DataPublicacao = System.currentTimeMillis();
        this.UserId = userid;

    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public void setCadeira(String cadeira) {
        Cadeira = cadeira;
    }

    public void setUniversidade(String universidade) {
        Universidade = universidade;
    }

    public String getNome() {
        return Nome;
    }

    public String getCadeira() {
        return Cadeira;
    }

    public String getUniversidade() {
        return Universidade;
    }

    public int getTotalFotos() {
        return TotalFotos;
    }

    public void setTotalFotos(int totalFotos) {
        TotalFotos = totalFotos;
    }

    public void setDataPublicacao(long dataPublicacao) {
        this.DataPublicacao = dataPublicacao;
    }

    public int getAcessos() {
        return Acessos;
    }

    public void incrementAcessos(){
        this.Acessos++;
    }

    public long getDataPublicacao() {
        return DataPublicacao;
    }

    public void setAcessos(int nAcessos) {
        this.Acessos = nAcessos;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
