package com.example.yogatama.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class DapatkanAlamatTaks extends AsyncTask<Location, Void, String> {
    private Context mContext;
    private onTaskSelesai mListener;



    public DapatkanAlamatTaks(Context applicationContext,onTaskSelesai listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Location... locations) {
        Geocoder geocoder=new Geocoder(mContext, Locale.getDefault());
        Location location=locations[0];
        List<Address> alamat = null;
        String resultMessage="";
        try {
            alamat=geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException ioException){
            resultMessage="tdk tersedia";
            Log.e("lokasi eror", resultMessage,ioException);
        } catch (IllegalArgumentException illegalArgumentException){
            resultMessage="Koordinat tdk valid";
            Log.e("lok eror",resultMessage+"."+
            "Latitude= "+location.getLatitude()+
            ", Longidue= "+location.getLongitude(),illegalArgumentException);
        }
        if (alamat == null || alamat.size() == 0)
        {
            if (resultMessage.isEmpty())
            {
                resultMessage = "Alamat tidak ditemukan";
                Log.e("Lokasi Error",resultMessage);
            }
        }
        else
        {
            Address address = alamat.get(0);
            ArrayList<String> barisAlamat = new ArrayList<>();

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
            {
                barisAlamat.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join("\n",barisAlamat);
        }
        return resultMessage;
    }

    interface onTaskSelesai
    {
        void onTaskCompleted(String result);
    }

    @Override
    protected void onPostExecute(String alamat)
    {
        mListener.onTaskCompleted(alamat);
        super.onPostExecute(alamat);
    }


}
