package in.co.iodev.formykerala.Activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Iterator;

import in.co.iodev.formykerala.Constants.Constants;
import in.co.iodev.formykerala.Controllers.CheckInternet;
import in.co.iodev.formykerala.Controllers.HTTPPostGet;
import in.co.iodev.formykerala.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.lang.Boolean.TRUE;
public class AcceptedItemFragment extends Fragment {

    SharedPreferences sharedPref;
    String url= Constants.Get_Accepted_Request;
    String url2=Constants.Send_Donation_items;
    JSONArray Mainproducts,products;
    ListView product_request_list;
    String TimeIndex;
    String StringData;
    Product_Request_Adapter adapter;
    Boolean submit=false;
    Button Logout;
    ImageView search_button;
    EditText item_search;
    JSONObject items;
    Context context;
    ProgressDialog progress;
    Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accepted_item, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref=getDefaultSharedPreferences(getContext());
        context=getContext();

        TimeIndex=sharedPref.getString("TimeIndex","");

        items=new JSONObject();
        JSONObject timeindex=new JSONObject();
        try {
            timeindex.put("TimeIndex",TimeIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringData=timeindex.toString();
        submit=false;
        Log.d("sj",StringData.toString());

        product_request_list=view.findViewById(R.id.donor_items_edit_listview);
        adapter=new Product_Request_Adapter();
        logout=view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref=getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("TimeIndex");
                editor.commit();
                sharedPref.edit().apply();
                startActivity(new Intent(getContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });


    }

//        private void search() {
//            if(!item_search.getText().toString().equals(""))
//            {products.clear();
//                for (int i=0;i<Mainproducts.size();i++)
//                {
//                    if(Mainproducts.get(i).equals(item_search.getText().toString()))
//                    {
//                        products.add(Mainproducts.get(i));
//
//                    }
//                }}
//            else {
//                products.clear();
//                products.addAll(Mainproducts);
//            }
//            product_request_list.setAdapter(adapter);
//            Log.d("Items",items.toString()+" "+products.toString()+" "+Mainproducts.toString());
//
//        }

    public void request(View view) {

    }

    public void view_items(View view) {
    }


    private class Product_Request_Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return products.length();
        }

        @Override
        public Object getItem(int position) {
            Object o=null;
            try {


                o= products.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return o;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder1 holder= null;

            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.acceptor_list_items,parent,false);
                holder =new ViewHolder1(view);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder1) view.getTag();
            }


            try {
                final ViewHolder1 finalHolder = holder;

                JSONObject object=products.getJSONObject(position);
                JSONObject object1=object.getJSONObject("Items");
                String product="",qty="";
                Iterator<String> iter = object1.keys();
                while (iter.hasNext()) {
                    String key = iter.next();

                    product+="\n"+key;

                    try {
                        qty+="\n"+object1.get(key);
                    } catch (JSONException e) {
                        // Something went wrong!
                    }}

                finalHolder.ProductName.setText(String.valueOf(object.getString("Name")));

                finalHolder.Quantity.setText(qty);
                finalHolder.Product.setText(product);
                finalHolder.Phone.setText(object.getString("PhoneNumber"));
                   /* if(items.has(holder.ProductName.getText().toString())) {
                        Log.d("Items",items.getString(holder.ProductName.getText().toString()));
                        holder.Quantity.setText(items.getString(holder.ProductName.getText().toString()));
                        holder.selected.setChecked(true);

                    }
                    else {
                        holder.selected.setChecked(FALSE);
                        holder.Quantity.setText("");
                    }*/



            }catch (Exception e){
            }



            return view;
        }
    }
    private class ViewHolder1 {
        TextView ProductName,Quantity,Product,Phone;



        public ViewHolder1(View v) {
            ProductName = (TextView) v.findViewById(R.id.product_name);
            Quantity=v.findViewById(R.id.requested_quantity);
            Product=v.findViewById(R.id.products);
            Phone=v.findViewById(R.id.phone_no);
        }
    }

    private class HTTPAsyncTask3 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response="Network Error";
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    if(!submit)
                        response= HTTPPostGet.getJsonResponse(url,StringData);
                    else
                        response= HTTPPostGet.getJsonResponse(url2,StringData);
                    Log.d("sj2",StringData.toString());
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPreExecute() {
            CheckInternet CI=new CheckInternet();
            CI.isOnline(context);
            logout.setClickable(false);
            progress=new ProgressDialog(context);
            String loadingMessage = getString(R.string.loading);
            progress.setMessage(loadingMessage);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progress.cancel();
            logout.setClickable(true);
            JSONObject responseObject= null;
            try {
                if (!submit)
                { Log.d("Responseitem1",result.toString());


                    JSONArray parentObject = new JSONArray(result);
                    products = new JSONArray();
                    products=parentObject;
                    if(products.length()==0)
                    {
                         product_request_list.setVisibility(View.INVISIBLE);
                         getView().findViewById(R.id.no_entry).setVisibility(View.VISIBLE);
                    }
                    else {
                        product_request_list.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.no_entry).setVisibility(View.INVISIBLE);
                    }
                    Log.d("Responseitem",result.toString());
                    Log.d("Responseitem4",products.toString());
                    product_request_list.setAdapter(adapter);

                }
                else
                {Log.d("Responseitem",result);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putBoolean(TimeIndex+"Edited", TRUE);
                    editor.commit();


                    submit=false;


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }    }


    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            new HTTPAsyncTask3().execute(url);        }else{
            // fragment is no longer visible
        }
    }
}
