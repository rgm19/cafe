package a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.VistaPedido;
import dmax.dialog.SpotsDialog;

public class ActivityPedidos extends AppCompatActivity {

    AlertDialog dialogo;
    private ListView listView;

    ActualizacionPedidos actualizacionPedidos;


    ArrayList<VistaPedido> vistaPedidos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        if((this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                <= Configuration.SCREENLAYOUT_SIZE_LARGE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }//Fin onCreate

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * Actualiza la base de datos y lanza el adapter5
         */
        dialogo = new SpotsDialog(this,  "Cargando pedidos...");
        dialogo.show();

        vistaPedidos.clear();

        String consulta = "select nom_pro,num_pedido, idProducto, idCliente, complementos, hora, cantidad, productos.precio, estado, username from pedidos, usuarios,productos where usuarios.id_cli=" +
                "pedidos.idCliente and productos.id_pro=pedidos.idProducto;";
        new ConsultasPedidos(consulta, dialogo).execute();
        actualizacionPedidos = new ActualizacionPedidos();
        actualizacionPedidos.execute();
    }

    /**
     * Lanza el Adapter del listView con todos los pedidos actuales
     */
    private void lanzarAdapter() {
        listView = (ListView) findViewById(R.id.lvAPedidos);
        listView.setAdapter(new AdapterPedidos(vistaPedidos));
        itemListener();
    }//Fin lanzarAdapter

    /**
     * Manda por un bundle la información de ese activity
     */
    private void itemListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogo.show();

                Intent intent = new Intent(ActivityPedidos.this,ActivityPedidosDetalles.class);
                intent.putExtra("nom_pro",vistaPedidos.get(position).getNombre());
                intent.putExtra("estado",vistaPedidos.get(position).getEstado());
                intent.putExtra("hora",vistaPedidos.get(position).getHora());
                intent.putExtra("precio",vistaPedidos.get(position).getPrecioTotal());
                intent.putExtra("idCliente",vistaPedidos.get(position).getId());
                intent.putExtra("nproducto",vistaPedidos.get(position).getStringProducto());
                intent.putExtra("complementos", vistaPedidos.get(position).getComplementos());
                startActivity(intent);

            }
        });
        /**
         *Salir del activityPedidos.
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String nombre = vistaPedidos.get(position).getNombre();
                final String hora = vistaPedidos.get(position).getHora();
                new android.support.v7.app.AlertDialog.Builder(ActivityPedidos.this)
                        .setTitle("Finalizar Pedido")
                        .setMessage("Â¿Finalizar y eliminar el pedido?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                borrarPedido(nombre,hora);
                            }
                        }).create().show();
                return true;
            }
        });
    }//Fin itemlistener



    /**
     * Borra todos los pedidos del usuario y hora seleccionados para limpiar la lista de pedidos
     * finalizados
     * @param usuario
     * @param hora
     */
    public void borrarPedido(String usuario, String hora){
        String delete = "delete from pedidos where idCliente = (Select id_cli " +
                "from usuarios where username = '" + usuario + "') " +
                "and hora = '" + hora + "'";
        new ConsultasPedidos(delete, dialogo).execute();
    }

    /**
     * Captura la acción de pulsar el botón de atrás y vuelve a la pantalla de login
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar la sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        actualizacionPedidos.cancel(true);
                        finish();
                        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                }).create().show();
    }//Fin onBackPressed

////////////////////////////////////////////////////////////////////////////////////////////////////

    public class ConsultasPedidos extends AsyncTask<Void,Void,ResultSet> {

        android.app.AlertDialog dialog;
        String consultaPd;
        Connection conexPd;
        Statement sentenciaPd;
        ResultSet resultPd;

        public ConsultasPedidos(String consulta, android.app.AlertDialog dialog){
            this.consultaPd=consulta;
            this.dialog=dialog;
        }

        @Override
        protected ResultSet doInBackground(Void... params) {
            try {
                conexPd = DriverManager.getConnection("jdbc:mysql://" + ActivityLogin.ip + "/base20171",
                        "ubase20171", "pbase20171");
                sentenciaPd = conexPd.createStatement();
                resultPd = null;
                publishProgress();

                if (consultaPd.startsWith("select"))
                    resultPd = sentenciaPd.executeQuery(consultaPd);

                if (consultaPd.startsWith("update") || consultaPd.startsWith("delete")){
                    sentenciaPd.executeUpdate(consultaPd);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultPd;
        }

        /**
         * Va añadiendo al arraylist de vistapedidos, los datos de la bbdd sobre la consulta.
         * @param resultSet
         */
        @Override
        protected void onPostExecute(ResultSet resultSet) {
            super.onPostExecute(resultSet);
            try{
                if (consultaPd.contains("precio")){
                    while (resultSet.next()){
                        vistaPedidos.add(new VistaPedido(resultSet.getString("username"),
                                resultSet.getTime("hora").toString(), resultSet.getFloat("precio"),resultSet.getInt("estado"),resultSet.getInt("idCliente"),resultSet.getString("nom_pro"),resultSet.getString("complementos")));
                    }
                    lanzarAdapter();
                }

                if (consultaPd.contains("complementos")) {
                    int idCli = 0;
                    String usuario = null;
                    String hora = null;


                }
                conexPd.close();
                sentenciaPd.close();
                resultPd.close();


            }catch (Exception ex) { Log.d("Fallo de cojones",ex.getMessage()); }
            dialog.dismiss();
        }
    }//Fin AsynTack


    ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  ///////////////////////////////////////////////////////////////////////////////////////////
     */
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public class ActualizacionPedidos extends AsyncTask<Void,Void,Void> {

        public ActualizacionPedidos(){  }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids)
        {
            super.onProgressUpdate();
            onStart();
            dialogo.dismiss();
        }
    }//Fin AsynTack

}//Fin Activity
