package app.vitamiin.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.vitamiin.com.Model.IngredientInfo;
import app.vitamiin.com.R;

/**
 * Created by Peter on 2015-08-20.
 */
public class MaterialListAdapter extends ArrayAdapter<ArrayList<IngredientInfo>> {
    private Context m_context;

    public MaterialListAdapter(Context context, ArrayList<ArrayList<IngredientInfo>> arrayItem) {
        super(context, 0, arrayItem);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_material,
                    parent, false);
            itemHolder = new ItemHolder(convertView);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }

        itemHolder.showInfo(position);

        return convertView;
    }

    public class ItemHolder {

        TextView m_tvMaterial;
        ImageView m_imvAdditive, m_imvHarmful, m_imvEwg12;

        public ItemHolder(View v) {
            m_tvMaterial = (TextView) v.findViewById(R.id.tv_material);
            m_imvAdditive = (ImageView) v.findViewById(R.id.imv_additive);
            m_imvHarmful = (ImageView) v.findViewById(R.id.imv_harmful);
            m_imvEwg12 = (ImageView) v.findViewById(R.id.imv_ewg12);
        }

        public void showInfo(final int position) {
            final IngredientInfo arrInfo = getItem(position).get(0);
            m_tvMaterial.setText(arrInfo._name);
            switch (arrInfo._ewg){
                case 0:
                    m_tvMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_grey, 0, 0, 0);
                    break;
                case 1:
                    m_tvMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_blue, 0, 0, 0);
                    break;
                case 2:
                    m_tvMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_green, 0, 0, 0);
                    break;
                case 3:
                    m_tvMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_yellow, 0, 0, 0);
                    break;
                case 4:
                    m_tvMaterial.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_circle_red, 0, 0, 0);
                    break;
            }

            if(arrInfo._ewg12)
                m_imvEwg12.setVisibility(View.VISIBLE);
            else
                m_imvEwg12.setVisibility(View.GONE);

            if(arrInfo._harmful)
                m_imvHarmful.setVisibility(View.VISIBLE);
            else
                m_imvHarmful.setVisibility(View.GONE);

            if(arrInfo._type==4 || arrInfo._type==5)
                m_imvAdditive.setVisibility(View.VISIBLE);
            else
                m_imvAdditive.setVisibility(View.GONE);
        }
    }
}