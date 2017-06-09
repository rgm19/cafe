package a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria;

//import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Email.SendMail;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.BDFinal;
import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Email.SendMail;
import dmax.dialog.SpotsDialog;

public class ActivityPedidosDetalles extends AppCompatActivity {
    AlertDialog dialogo;
    private static boolean mail = false;
    private ListView listView;
    private TextView tvNombre, tvHora, precioT,tvNombreProductoDetalles,tvcomplementosdetalles;
    private FloatingActionButton fab;
    String nombre;
    private String hora,complementos,nombrePro;
    private float precioTotal;
    private int estado;
    int idCliente;

    /**
     * Recogemos el bundle que enviemos y lo mostramos.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_detalles);

        if((this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                <= Configuration.SCREENLAYOUT_SIZE_LARGE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        nombre= getIntent().getExtras().getString("nom_pro");
        hora= getIntent().getExtras().getString("hora");
        precioTotal= getIntent().getExtras().getFloat("precio");
        estado=getIntent().getExtras().getInt("estado");
        idCliente=getIntent().getExtras().getInt("idCliente");
        nombrePro=getIntent().getExtras().getString("nproducto");
        complementos=getIntent().getExtras().getString("complementos");
        inflar();
        tvNombre.setText(nombre);
        tvHora.setText(hora);
        String p = String.valueOf(precioTotal);
        precioT.setText(p);
        tvcomplementosdetalles.setText(complementos);
        tvNombreProductoDetalles.setText(nombrePro);
        listeners();


    }

    /**
     * Inflamos los atributos
     */
    private void inflar() {
        dialogo     = new SpotsDialog(this,  "Termiando pedidos...");

        listView    = (ListView) findViewById(R.id.lvAPedidosD);

        tvNombre    = (TextView) findViewById(R.id.tvAPedidosDNomCli);
        tvHora      = (TextView) findViewById(R.id.tvAPedidosDHora);
        precioT     = (TextView) findViewById(R.id.tvAPedidosDPrecioT);
        fab         = (FloatingActionButton) findViewById(R.id.fab_done);
        tvNombreProductoDetalles       = (TextView) findViewById(R.id.tvNombreProductoDetalles);
        tvcomplementosdetalles = (TextView) findViewById(R.id.tvcomplementosdetalles);

    }

    /**
     * Te limpia el arraypedidos
     */
    @Override
    public void onBackPressed() {
        BDFinal.pedidosFinal.clear();
        finish();
    }

    /**
     * Mandas el adapter del activity, y cuando pulse el fab te envia el correo.
     */
    private void listeners() {
        listView.setAdapter(new AdapterPedidosDetalles(BDFinal.pedidosFinal));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    String q = "select usuarios.mail from usuarios, pedidos where usuarios.id_cli=pedidos.idCliente and usuarios.id_cli="+idCliente;
                if (!mail){
                    String message = "Pedido preparado para las " + hora
                            + " con un precio total de " + precioT.getText();
                    SendMail sendEmail = new SendMail(ActivityPedidosDetalles.this,q, "Pedido C@fetería", message);

                    sendEmail.execute();
                    fab.setImageResource(R.drawable.ic_done);
                    mail = true;

                  String update = "update pedidos set estado = 2 where idCliente = " +
                           idCliente +
                          " and hora = '" +hora + "'";
                  new UpdatePedido(update, dialogo).execute();
              }else{



                   mail = false;
                   onBackPressed();
               }
            }
        });
    }



///////////////////////////////////////////////////////////////////////////////////////////////////

    public class UpdatePedido extends AsyncTask<Void,Void,Void> {

        String consultaDt;
        Connection conexDt;
        Statement sentenciaDt;
        android.app.AlertDialog dialog;

        public UpdatePedido(String consulta, android.app.AlertDialog dialog){
            this.consultaDt=consulta;
            this.dialog=dialog;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                conexDt = DriverManager.getConnection("jdbc:mysql://" + ActivityLogin.ip + "/base20171", "ubase20171", "pbase20171");
                sentenciaDt = conexDt.createStatement();
                publishProgress();

                if (consultaDt.startsWith("update"))
                    sentenciaDt.executeUpdate(consultaDt);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            try {
                conexDt.close();
                sentenciaDt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }


    }//Fin AsynTack
}//Fin Activity
