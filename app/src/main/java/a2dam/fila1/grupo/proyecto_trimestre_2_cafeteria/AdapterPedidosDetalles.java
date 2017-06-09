package a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import a2dam.fila1.grupo.proyecto_trimestre_2_cafeteria.Bd.Pedido;

public class AdapterPedidosDetalles extends BaseAdapter {
    ArrayList<Pedido> pedido ;
    private View listItemView;

    public AdapterPedidosDetalles(ArrayList<Pedido> pedido) {
        this.pedido = pedido;
    }

    /**
     *
     * @return tamaño total del array
     */
    @Override
    public int getCount() {
        return pedido.size();
    }

    /**
     *
     * @param i
     * @return el pedido de i
     */
    @Override
    public Pedido getItem(int i) {
        return pedido.get(i);
    }

    /**
     *
     * @param i
     * @return devuelve el item id
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        listItemView = view;
        if (listItemView == null)
            listItemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_producto, null);

        ((TextView)listItemView.findViewById(R.id.tv_dt_list_nombre)).setText(getItem(i).getProducto().getNombre());
        ((TextView)listItemView.findViewById(R.id.tvListPedidosPrecio)).setText("" + getItem(i).getPrecio() + " €");
        ((TextView)listItemView.findViewById(R.id.tv_dt_list_cantidad)).setText("x" + getItem(i).getCantidad());
        ((TextView)listItemView.findViewById(R.id.tvApedidosDC)).setText("x" + getItem(i).getProducto());
        ((TextView)listItemView.findViewById(R.id.tv_dt_list_comentarios)).setText(getItem(i).getComentarios());

        if (!getItem(i).getComentarios().contains("lactosa")){
            (listItemView.findViewById(R.id.ivListPedidosAlert)).setVisibility(View.GONE);
        }
        if (ActivityLogin.USER.getCategoria()<2){
            listItemView.findViewById(R.id.ibListDelete).setVisibility(View.GONE);
        }

        return listItemView;
    }
}
