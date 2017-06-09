package a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria;

import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.VistaPedido;

/**
 * Created by Raquel.

 * Adapter para el listView de pedidos
 */

public class AdapterPedidos extends BaseAdapter {
    public static ArrayList<VistaPedido> pedidos;

    private View listItemView;
    private TextView tvNombre, tvHora, tvPrecio,tvApedido;
    private ConstraintLayout fondo;

    public AdapterPedidos(ArrayList<VistaPedido> pedidos) {
        this.pedidos = pedidos;
    }

    /**
     *
     * @return tamaño total de pedidos
     */
    @Override
    public int getCount() {
        return pedidos.size();
    }

    /**
     *
     * @param i
     * @return te devuelve el pedido según la posición
     */
    @Override
    public VistaPedido getItem(int i) {
        return pedidos.get(i);
    }

    /**
     *
     * @param i
     * @return devuelve i
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     *
     * @param i
     * @param view
     * @param viewGroup
     * @return la vista
     */
    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        listItemView = view;
        if (listItemView == null)
            listItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pedido, null);

        inflar();

        final String nombre = getItem(i).getNombre();
        final String hora = getItem(i).getHora();
        final String producto =getItem(i).getStringProducto();

        tvNombre.setText(nombre);
        tvPrecio.setText(getItem(i).getPrecioTotal() + "€");
        tvHora.setText(hora);
        tvApedido.setText(producto);

        switch (getItem(i).getEstado()){
            case 1: fondo.setBackgroundColor(0xffe1b1b1);
                break;
            case 2: fondo.setBackgroundColor(0xff90d78f);
                break;
            default:
        }

        return listItemView;
    }

    /**
     *  Infla todos los elementos del layout del activity
     */
    private void inflar() {
        tvNombre = (TextView) listItemView.findViewById(R.id.tvListPedidosNombre);
        tvHora = (TextView) listItemView.findViewById(R.id.tvListPedidosHora);
        tvPrecio = (TextView) listItemView.findViewById(R.id.tvListPedidosPrecio);
        fondo = (ConstraintLayout) listItemView.findViewById(R.id.layout);
        tvApedido = (TextView) listItemView.findViewById(R.id.tvApedidosDC);
    }

}//Fin Adapter
