package iul.iscte.daam_backpack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListAnexoAdapter extends RecyclerView.Adapter<ListAnexoAdapter.ResumosViewHolder>{

    private ArrayList<Resumo> resumos;

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference().child("Resumos");

    public ListAnexoAdapter(ArrayList<Resumo> resumos){
        this.resumos = resumos;
    }

    @NonNull
    @Override
    public ResumosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_meus_ficheiros, parent, false);
        ResumosViewHolder imageViewHolder = new ResumosViewHolder(v);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResumosViewHolder holder, final int position) {
        Resumo obj = resumos.get(position);
        final String nomeResumo = obj.getNome();
        final String cadeiraResumo = obj.getCadeira();
        final String universidadeResumo = obj.getUniversidade();
        final String totalFotosString = String.valueOf(obj.getTotalFotos());

        holder.mRegistoNome.setText(nomeResumo);
        holder.mRegistoUnidadeCurricular.setText(cadeiraResumo);
        holder.mRegistoUniversidade.setText(universidadeResumo);
        holder.mRegistoButao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.child(nomeResumo).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Resumo value = dataSnapshot.getValue(Resumo.class);
                        if(value != null){
                            value.incrementAcessos();
                            dataSnapshot.getRef().setValue(value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(v.getContext() ,CheckAnexo_Activity.class);
                intent.putExtra("nomeResumo", nomeResumo);
                intent.putExtra("cadeiraResumo", cadeiraResumo);
                intent.putExtra("universidadeResumo", universidadeResumo);
                intent.putExtra("totalFotos", totalFotosString);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return resumos.size();
    }


    public static class ResumosViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView mRegistoNome;
        public TextView mRegistoUnidadeCurricular;
        public TextView mRegistoUniversidade;
        public ImageButton mRegistoButao;


        public ResumosViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mRegistoNome = (TextView) itemView.findViewById(R.id.nomeRegistoLista);
            mRegistoUnidadeCurricular = (TextView) itemView.findViewById(R.id.cadeiraRegistoLista);
            mRegistoUniversidade = (TextView) itemView.findViewById(R.id.universidadeRegistoLista);
            mRegistoButao = (ImageButton) itemView.findViewById(R.id.checkmoreButton);

        }
    }

}
