package br.com.kotar.core.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.kotar.core.helper.handler.Handler;
import br.com.kotar.core.helper.map.EnderecoCoordenadaHelper;
import br.com.kotar.core.helper.map.LatLng;
import br.com.kotar.core.util.http.HttpUtil;

public class MapUtil {

    private static Calendar lastCall = null;

    public static EnderecoCoordenadaHelper buscarLatitudeLongitude(String rua, String numero, String cidade, String siglaUf) {
    	EnderecoCoordenadaHelper coordenada = null;
        try{
        	ObjectMapper mapper = new ObjectMapper();
    		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

    		
        	
            String address = rua + ", " + numero + ", " + cidade + ", " + siglaUf;

            String parametros = address;
            parametros = parametros.toUpperCase();
            parametros = StringUtil.retirarTodosOsEspacosSobrando(parametros);
            parametros = StringUtil.tiraAcentos(parametros);

            checkLastCall();

            String url = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAfBKUs9hvWIVDbiHWNbAXNsr9HIXtn6VM&sensor=true&address=" + URLEncoder.encode(parametros, "UTF-8");
            
            String json = HttpUtil.getHttpRequest(url);
            
            HashMap<String,Object> o = mapper.readValue(json, typeRef);
            
            //JSONObject jsonObj = new JSONObject(json);
            if (o != null) {
                ArrayList<HashMap<String,Object>> results = (ArrayList<HashMap<String,Object>>) o.get("results");
                if (results != null && results.size() > 0) {
                	HashMap<String,Object> result0 = (HashMap<String,Object>) results.get(0);

                    if (result0 != null) {
                    	HashMap<String,Object> geometry = (HashMap<String,Object>) result0.get("geometry");

                        if (geometry != null) {
                        	HashMap<String,Object> location = (HashMap<String,Object>) geometry.get("location");

                            if (location != null) {

                                java.lang.Double lat = (java.lang.Double) location.get("lat");
                                java.lang.Double lng = (java.lang.Double) location.get("lng");

                                coordenada = new EnderecoCoordenadaHelper();
                                coordenada.setDsCidade(cidade);
                                coordenada.setDsLogradouro(rua);
                                coordenada.setNrNumero(numero);
                                coordenada.setNrLatitude(lat.toString());
                                coordenada.setNrLongitude(lng.toString());

                            }
                        }
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return coordenada;
    }
    
    Integer distancia = null;
    int mDelay = 1000;
    int maxTentativas;
    public Integer calcularDistanciaGoogleMaps(LatLng origin, LatLng dest, int tentativas) {
    	
    	this.maxTentativas = tentativas;
    	Handler mHandler = new Handler();    	

    	// try request
    	mHandler.postDelayed(mDelay, new Runnable() {
    	   public void run() {
    	          		   
    		  try {
    			  distancia = calcularDistanciaGoogleMapsInt(origin, dest);
    		  }
    		  catch (Exception e) {
    	          mDelay *= 1.6;  // or as per your formula
    	          
    	          if (maxTentativas > 0) {
    	        	  maxTentativas--;
    	        	  mHandler.postDelayed(mDelay, this);
    	          }
    		  }    		   
    	   }
    	});
    	
    	return distancia;
    }

    private static Integer calcularDistanciaGoogleMapsInt(LatLng origin, LatLng dest) throws Exception{
    	Integer retorno = null;
    
    	try{
    		checkLastCall();
    		
        	ObjectMapper mapper = new ObjectMapper();
    		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
    		
    		String url = getDirectionsUrl(origin, dest);    	
    		String json = HttpUtil.getHttpRequest(url);
    		
    		HashMap<String,Object> o = mapper.readValue(json, typeRef);
    		
    		if (o != null) {
    			
    			//DirectionsJSONParser parser = new DirectionsJSONParser();  //se precisar utilizar as rotas calculadas//
            	ArrayList<HashMap<String,Object>> routes = (ArrayList<HashMap<String,Object>>) o.get("routes");
            	HashMap<String,Object> routes2 = (HashMap<String,Object>)routes.get(0);
            	ArrayList<HashMap<String,Object>> legs = (ArrayList<HashMap<String,Object>>) routes2.get("legs");
            	HashMap<String,Object> steps = (HashMap<String,Object>) legs.get(0);
            	HashMap<String,Object> distance = (HashMap<String,Object>) steps.get("distance");
            	
	            //String distancia = (String) distance.get("text");
	
	            Integer distanciaMetros = (Integer) distance.get("value");
	            
	            retorno = distanciaMetros;
	            //routes = parser.parse(jObject);
    		
    		
    		}
    		
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println(" ERRO NO CALCULO DA DISTANCIA: Origem: "+origin.latitude+" - "+origin.longitude);
            System.out.println(" ERRO NO CALCULO DA DISTANCIA: Destino: "+dest.latitude+" - "+dest.longitude);
            throw e;
        }
    	
    	return retorno;
    }
    
    
    
    private static String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
     
    private static synchronized void checkLastCall(){
        if (lastCall != null){
            Calendar data = Calendar.getInstance();

            long diff = data.getTimeInMillis() - lastCall.getTimeInMillis();
            if (diff < 1000){
                System.out.println("google geo waiting...");
                pause(diff);
            }
        }
        lastCall = Calendar.getInstance();
    }

    private static synchronized void pause(long time){
        try{
            Thread.sleep(time);
        }
        catch(Exception e){
        }
    }
    
    
    

}
