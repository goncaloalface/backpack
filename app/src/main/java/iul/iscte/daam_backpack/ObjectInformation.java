package iul.iscte.daam_backpack;

import android.net.Uri;

public class ObjectInformation {

    private String NomeFicheiro;
    private Uri Uri;

    public ObjectInformation(String nomeFicheiro, Uri uri){
        this.NomeFicheiro = nomeFicheiro;
        this.Uri = uri;
    }

    public String getNomeFicheiro() {
        return NomeFicheiro;
    }

    public Uri getUri() {
        return Uri;
    }

    public void setUri(Uri uri) {
        Uri = uri;
    }

    public void setNomeFicheiro(String nomeFicheiro) {
        NomeFicheiro = nomeFicheiro;
    }
}
