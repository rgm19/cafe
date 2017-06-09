package a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria;


import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.BDFinal;
import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.Pedido;
import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.Producto;
import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Dialog.Dialog_menu;
import dmax.dialog.SpotsDialog;

public class ActivityCafe extends AppCompatActivity {

    private AlertDialog dialogo;// Dialogo

    private Spinner spTipo;     //Tipo de cafe hola
    private Spinner spLeche;    //Temperatura leche
    private Spinner spAzucar;   //Tipo de azucar

    private CheckBox lactosa;
    private CheckBox crema;
    private CheckBox chocolate;
    private CheckBox hielo;

    private TextView precio;
    private TextView cantidad;

    private ImageButton volver;
    private ImageButton menu;

    private Button pedir;
    private Button menos;
    private Button mas;


    private FloatingActionButton fab;
    private FloatingActionButton fab_sesion;

    ArrayAdapter<CharSequence> adapterLeche;    //Adapter para el spinner de la temperatura de la leche
    ArrayAdapter<CharSequence> adapterAzucar;   //Adapter para el spinner del tipo de azucar
    ArrayAdapter adapterTipo;           //Adapter para el spinner de tipos de cafe


    static ArrayList<String> arrayTipo = new ArrayList<>();
    static float precioCafeFinal;
    static boolean datos = false;
    static boolean leche = false;
    static String datosPedido;
    static int id;
    static String nombre;
    static int cant;
    static float precioCafe = 0f;//Precio cafe

    /**
     * Llamamos a todos los métodos para su ejecución
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);

        if((this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                <= Configuration.SCREENLAYOUT_SIZE_LARGE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        inflar();
        listenerBotones();
        inflarSpinnerTipoCafe();
    }//Fin onCreate

    /**
     * Vaciamos el arrayTipo, datos lo ponemos false
     */

    @Override
    protected void onStart() {
        super.onStart();
        arrayTipo.clear();
        datos = false;

    }

    /**
     * Activa el Dialog con el String del parametro
     * @param texto
     */
    private void lanzarDialogo(String texto) {
        dialogo = new SpotsDialog(this, texto);
        dialogo.show();
    }

    /**
     * Infla todos los elementos del layout del activity
     */
    private void inflar() {
        spTipo       = (Spinner)     findViewById(R.id.sp_cf_tipo);
        spLeche      = (Spinner)     findViewById(R.id.sp_cf_leche);
        spAzucar     = (Spinner)     findViewById(R.id.sp_cf_azucar);

        lactosa      = (CheckBox)    findViewById(R.id.cb_cf_lactosa);
        crema        = (CheckBox)    findViewById(R.id.cb_cf_crema);
        chocolate    = (CheckBox)    findViewById(R.id.cb_cf_choco);
        hielo        = (CheckBox)    findViewById(R.id.cb_cf_hielo);

        precio       = (TextView)    findViewById(R.id.tv_cf_precioNum);
        cantidad     = (TextView)    findViewById(R.id.tv_cnt_cantidad);

        volver       = (ImageButton) findViewById(R.id.ib_cf_volver);
        menu         = (ImageButton) findViewById(R.id.ib_cf_menu);

        pedir        = (Button)      findViewById(R.id.btn_cf_pedir);
        menos        = (Button)      findViewById(R.id.btn_cnt_menos);
        mas          = (Button)      findViewById(R.id.btn_cnt_mas);

        fab          = (FloatingActionButton) findViewById(R.id.fab_cf);
        fab_sesion   = (FloatingActionButton) findViewById(R.id.fab_sesion);

        adapterLeche = ArrayAdapter.createFromResource(this, R.array.leche , android.R.layout.simple_spinner_dropdown_item);
        adapterAzucar= ArrayAdapter.createFromResource(this, R.array.azucar, android.R.layout.simple_spinner_dropdown_item);

        adapterTipo  = ArrayAdapter.createFromResource(this, R.array.productos, android.R.layout.simple_spinner_dropdown_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLeche .setAdapter(adapterLeche);
        spAzucar.setAdapter(adapterAzucar);
        spTipo.setAdapter(adapterTipo);

    }//Fin inflar

    /**
     * Captura la acción de pulsar el botón atrás y vuelve a la pantalla de login, borrando los pedidos guardados
     */

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("Si sale se cerrará la sesión y se perderán los pedidos no realizados\n¿Continuar?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityLogin.USER=null;
                        BDFinal.pedidosFinal.clear();
                        finish();
                    }
                }).create().show();
    }



    /**
     * Métodos de los spinner, según la posición es un producto u otro
     */
    private void inflarSpinnerTipoCafe() {
        lanzarDialogo("Calculando precios...");



        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position){
                        case 0:
                                precioCafe =0;
                                setPrecio();
                                llevaLeche(false);
                                dialogo.dismiss();
                                ActivityCafe.id=0;

                            break;

                        case 1:
                            String a;
                            precioCafe =0.90F;
                            a=setPrecio();
                            nombre="Cortado";
                            llevaLeche(true);
                            datosPedido=datosPedido+nombre+a;
                            ActivityCafe. id=1;


                            break;
                        case 2:
                            String b;
                            precioCafe =0.90F;
                            b=setPrecio();
                            nombre="Con Leche";
                            llevaLeche(true);
                            datosPedido=datosPedido+nombre+b;
                            ActivityCafe.  id=2;
                            break;

                        case 3:
                            String c;
                            precioCafe =0.90F;
                            c=setPrecio();

                            nombre="Manchada";
                            llevaLeche(true);
                            ActivityCafe.id=3;
                            datosPedido=datosPedido+nombre+c;
                            break;

                        case 4:
                            String d;
                            precioCafe =0.90F;
                            d=setPrecio();
                            nombre="Solo";
                            llevaLeche(false);
                            ActivityCafe.id=4;
                            datosPedido=datosPedido+nombre+d;
                            break;
                        case 5:
                            String e;
                            precioCafe =1F;
                            e=setPrecio();
                            nombre="Bombón";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+e;
                            ActivityCafe.id=5;

                            break;
                        case 6:
                            String f;
                            precioCafe =0.90F;
                            f=setPrecio();
                            ActivityCafe.id=6;

                            nombre="Desca. sobre";
                            llevaLeche(true);
                            datosPedido=datosPedido+nombre+f;
                            break;
                        case 7:
                            String g;
                            precioCafe =0.90F;
                            g=setPrecio();
                            ActivityCafe. id=7;

                            nombre="Desca. maquina";
                            llevaLeche(true);
                            datosPedido=datosPedido+nombre+g;
                            break;
                        case 8:
                            String h;
                            precioCafe =1F;
                            h=setPrecio();
                            ActivityCafe.id=8;

                            nombre="ColaCao";
                            llevaLeche(true);
                            datosPedido=datosPedido+nombre+h;
                            break;
                        case 9:
                            String i;
                            precioCafe =0.90F;
                            i=setPrecio();
                            ActivityCafe.id=9;

                            nombre="Infusión manz";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+i;
                            break;

                        case 10:
                            String j;
                            precioCafe =0.90F;
                            j=setPrecio();
                            ActivityCafe.id=10;

                            nombre="Infusión Menta-Poleo";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+j;
                            break;


                        case 12:
                            String k;
                            precioCafe =0.90F;
                            k=setPrecio();
                            ActivityCafe.id=12;

                            nombre="Infusión Tila";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+k;
                            break;

                        case 13:
                            String l;
                            precioCafe =0.90F;
                            l=setPrecio();
                            ActivityCafe.id=13;
                            nombre="Infusión Té Rojo";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+l;
                            break;

                        case 14:
                            String ñ;
                            precioCafe =0.90F;
                            ñ=setPrecio();
                            ActivityCafe.id=14;

                            nombre="Infusión Té Verde";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+ñ;
                            break;

                        case 15:
                            String o;
                            precioCafe =0.90F;
                            o=setPrecio();
                            ActivityCafe.id=15;

                            nombre="Infusión Té";
                            llevaLeche(false);
                            datosPedido=datosPedido+nombre+o;
                            break;
                        default: dialogo.show();

                    }
                }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //No se usa pero no se puede borrar
            }
        });//Fin Spinner Tipo
    }

    /**
     * Métodos lístener de todos los botones del layout
     */
    private void listenerBotones() {

        menu.setOnClickListener(new View.OnClickListener() {//Muestra la imagen con los cafes disponibles
            @Override
            public void onClick(View v) {
                Dialog_menu dialogoMenu= new Dialog_menu();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialogoMenu.setCancelable(true);
                dialogoMenu.show(ft, "Menú");
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {//Boton volver
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        menos.setOnClickListener(new View.OnClickListener() {//Boton menos resta un cafe
            @Override
            public void onClick(View v) {
                setCantidad(-1);
            }
        });


        mas.setOnClickListener(new View.OnClickListener() {//Boton mas agrega un cafe
            @Override
            public void onClick(View v) {
                setCantidad(1);
            }
        });
        fab_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(myintent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spTipo.getSelectedItemPosition() != 0){
                    precioCafeFinal=Float.parseFloat(setPrecio());
                    Producto producto = new Producto(ActivityCafe.id,nombre,precioCafeFinal,leche);
                    Pedido pedido = new Pedido(producto,cant+1,precioCafeFinal, generarComentarios());
                    BDFinal.pedidosFinal.add(pedido);
                    Toast.makeText(getApplicationContext(),"Añadido a la cesta",Toast.LENGTH_SHORT).show();

                    limpiar();
                }else
                    Toast.makeText(getApplicationContext(),"Debe seleccionar un TIPO de café",Toast.LENGTH_SHORT).show();
            }
        });

 pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Lanza la activity detalles para conocer el resumen del pedido
                if (BDFinal.pedidosFinal.size() == 0){
                    Toast.makeText(getApplicationContext(),"Debe añadir al menos un producto",Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(getApplicationContext(), ActivityDetalles.class);
                    startActivity(i);
                }
            }
        });

    }//Fin ListenerBotones

    /**
     * Limpiar, al realizar un pedido con FAB, desmarca los checkBox y coloca los spinner  y la
     * cantidad en posicion inicial
     */
    private void limpiar() {
        spTipo      .setSelection(0);
        spLeche     .setSelection(0);
        spAzucar    .setSelection(0);
        lactosa     .setChecked(false);
        crema       .setChecked(false);
        chocolate   .setChecked(false);
        hielo       .setChecked(false);
        cantidad    .setText(""+1);

        //setPrecio();
    }//Fin limpiar

    /**
     * Comprueba los checkbox y spinner seleccionados
     * @return String: Cadena con las opciones seleccionadas en spinner y checkbox
     */
    private String generarComentarios() {
        String comentarios = "";
        if (leche)
            comentarios = comentarios.concat(spLeche.getSelectedItem().toString().trim()+", ");
        comentarios = comentarios.concat(spAzucar.getSelectedItem().toString().trim()+"");
        if (leche)
            if (lactosa.isChecked())
                comentarios = comentarios.concat(", Sin lactosa");
        if (crema.isChecked())
            comentarios = comentarios.concat(", Crema");
        if (chocolate.isChecked())
            comentarios = comentarios.concat(", Chocolate");
        if (hielo.isChecked())
            comentarios = comentarios.concat(", Hielo");

        return comentarios;
    }//Fin generarComentarios

    /**
     * setCantidad, calcula y controla la cantidad de producto a pedir
     * @param i Cantidad a sumar o restar a la cantidad actual
     */
    private void setCantidad(int i) {
        cant = Integer.parseInt(cantidad.getText().toString().trim());
        if ((cant + i) < 1 || (cant + i) > 20 )
            Toast.makeText(getApplicationContext(), "Cantidad mínima 1, catidad máxima 20", Toast.LENGTH_SHORT).show();
        else
            cantidad.setText("" + (cant + i));

        setPrecio();
    }//Fin setCantidad

    /**
     * setPrecio, calcula el precio del pedido según el producto y las opciones seleccionadas
     * Redondea para que salga sólo con 2 decimales máximo
     */


    private String setPrecio(){
        String d;
        float precioFinal = precioCafe;
        if (leche)
            if (lactosa.isChecked())
                precioFinal += 0.2f;
        if (hielo.isChecked())
            precioFinal += 0.3f;
        if (crema.isChecked())
            precioFinal += 0.4f;
        if (chocolate.isChecked())
            precioFinal += 0.5f;

        precioFinal = precioFinal * Integer.parseInt(cantidad.getText().toString().trim());
        double redondeo = Math.round(precioFinal*100.0)/100.0;
        precio.setText(""+redondeo);
        return  d= String.valueOf(redondeo);
    }//Fin setPrecio

    /**
     * Habilita o deshabilita las opciones de leche según el producto
     * @param leche
     */
    private void llevaLeche(boolean leche) {
        spLeche.setEnabled(leche);
        lactosa.setEnabled(leche);
    }//Fin llevaLeche

    /**
     * Actualiza precio al detectar eventos de los checkbox (implementados en XML)
     * @param view
     */
    public void metodosCheked(View view) {
        setPrecio();
    }//Fin metodosCheked

////////////////////////////////////////////////////////////////////////////////////////////////////
}//Fin Acticity
