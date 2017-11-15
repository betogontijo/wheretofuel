package com.betogontijo.wheretofuel.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WhereToFuel implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	public void onModuleLoad() {
		loadMapApi();
	}

	private void loadMapApi() {
		boolean sensor = true;

		// load all the libs for use in the maps
		ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
		loadLibraries.add(LoadLibrary.ADSENSE);
		loadLibraries.add(LoadLibrary.DRAWING);
		loadLibraries.add(LoadLibrary.GEOMETRY);
		loadLibraries.add(LoadLibrary.PANORAMIO);
		loadLibraries.add(LoadLibrary.PLACES);
		loadLibraries.add(LoadLibrary.WEATHER);
		loadLibraries.add(LoadLibrary.VISUALIZATION);

		Runnable onLoad = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		LoadApi.go(onLoad, loadLibraries, sensor, "key=AIzaSyBeJwm8XRCSVgcxykX9hBtZ1_t0r7Kv08E");
	}

	/**
	 * See the map widgets for different map configurations
	 */
	private void draw() {
		drawDirections();
		// drawAutocomplete();
		// drawPlaces();
	}

	/**
	 * Add the widget to the demos
	 * 
	 * @param widget
	 *            map
	 */
	private void addMapWidget(Widget widget) {
		greetingService.greetServer("", new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				HTML html = new HTML(result);
				RootPanel.get().add(html);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});
		RootPanel.get().add(widget);
	}

	private void drawDirections() {
		DirectionsServiceMapWidget wMap = new DirectionsServiceMapWidget();
		addMapWidget(wMap);
	}

}
