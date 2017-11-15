package com.betogontijo.wheretofuel.client;

import java.util.ArrayList;

import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Geolocation.PositionOptions;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.placeslib.AutocompleteType;
import com.google.gwt.maps.client.placeslib.PlaceResult;
import com.google.gwt.maps.client.placeslib.PlaceSearchHandler;
import com.google.gwt.maps.client.placeslib.PlaceSearchPagination;
import com.google.gwt.maps.client.placeslib.PlaceSearchRequest;
import com.google.gwt.maps.client.placeslib.PlacesService;
import com.google.gwt.maps.client.placeslib.PlacesServiceStatus;
import com.google.gwt.maps.client.services.DistanceMatrixRequest;
import com.google.gwt.maps.client.services.DistanceMatrixRequestHandler;
import com.google.gwt.maps.client.services.DistanceMatrixResponse;
import com.google.gwt.maps.client.services.DistanceMatrixResponseElement;
import com.google.gwt.maps.client.services.DistanceMatrixResponseRow;
import com.google.gwt.maps.client.services.DistanceMatrixService;
import com.google.gwt.maps.client.services.DistanceMatrixStatus;
import com.google.gwt.maps.client.services.TravelMode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;

public class DirectionsServiceMapWidget extends Composite {

	interface PlaceProperties extends PropertyAccess<Place> {
		@Path("name")
		ModelKeyProvider<Place> key();

		@Path("distance")
		ValueProvider<Place, String> distance();
	}

	private VerticalPanel pWidget;
	private MapWidget mapWidget;
	private LatLng currentPos = LatLng.newInstance(-19.9283827, -43.9924392);
	private ListView<Place, String> flowPanel;

	public DirectionsServiceMapWidget() {
		pWidget = new VerticalPanel();
		createMap();

		RootPanel.get().add(pWidget);

		final PlaceProperties placeProperties = GWT.create(PlaceProperties.class);

		ListStore<Place> listStore = new ListStore<Place>(placeProperties.key());
		flowPanel = new ListView<Place, String>(listStore, placeProperties.distance());
		
		SelectionCell cell = new SelectionCell(new ArrayList<String>());
		flowPanel.setCell(cell);
		
		pWidget.add(flowPanel);

		initWidget(pWidget);

		getCurrentPosition();
	}

	private void getCurrentPosition() {
		Geolocation geolocation = Geolocation.getIfSupported();
		PositionOptions positionOptions = new PositionOptions();
		positionOptions.setHighAccuracyEnabled(true);
		geolocation.getCurrentPosition(new Callback<Position, PositionError>() {

			@Override
			public void onSuccess(Position result) {
				// TODO Auto-generated method stub
				currentPos = LatLng.newInstance(result.getCoordinates().getLatitude(),
						result.getCoordinates().getLongitude());
				searchRequest(currentPos);
			}

			@Override
			public void onFailure(PositionError reason) {
				// TODO Auto-generated method stub
				searchRequest(currentPos);
			}
		}, positionOptions);
	}

	private void createMap() {
		MapOptions opts = MapOptions.newInstance();
		opts.setZoom(13);
		opts.setCenter(currentPos);
		opts.setMapTypeId(MapTypeId.ROADMAP);

		mapWidget = new MapWidget(opts);

		pWidget.add(mapWidget);
	}

	private void searchRequest(LatLng clickLocation) {
		AutocompleteType[] types = new AutocompleteType[1];
		types[0] = AutocompleteType.GAS_STATION;

		PlaceSearchRequest request = PlaceSearchRequest.newInstance();
		request.setLocation(clickLocation);
		request.setRadius(5000d);
		// TODO add more AutocompleteTypes...
		// request.setTypes(AutocompleteType.ESTABLISHMENT);
		request.setTypes(types);

		PlacesService placeService = PlacesService.newInstance(mapWidget);
		placeService.nearbySearch(request, new PlaceSearchHandler() {

			@Override
			public void onCallback(JsArray<PlaceResult> results, PlaceSearchPagination pagination,
					PlacesServiceStatus status) {

				if (status == PlacesServiceStatus.OK) {
					// look up the details for the first place
					if (results.length() > 0) {
						for (int i = 0; i < results.length(); i++) {
							PlaceResult placeResult = results.get(i);
							getDistance(currentPos, placeResult.getGeometry().getLocation(), placeResult);
						}
					}
				} else {
					Window.alert("Status is: status=" + status);
				}
			}

		});
	}

	private void getDistance(LatLng origin, LatLng destination, final PlaceResult placeResult) {

		LatLng[] ao = new LatLng[1];
		ao[0] = origin;
		JsArray<LatLng> origins = ArrayHelper.toJsArray(origin);
		LatLng[] ad = new LatLng[1];
		ad[0] = destination;
		JsArray<LatLng> destinations = ArrayHelper.toJsArray(destination);

		DistanceMatrixRequest request = DistanceMatrixRequest.newInstance();
		request.setOrigins(origins);
		request.setDestinations(destinations);
		request.setTravelMode(TravelMode.DRIVING);

		DistanceMatrixService o = DistanceMatrixService.newInstance();
		o.getDistanceMatrix(request, new DistanceMatrixRequestHandler() {
			public void onCallback(DistanceMatrixResponse response, DistanceMatrixStatus status) {
				GWT.log("status=" + status.value());

				if (status == DistanceMatrixStatus.OK) {

					@SuppressWarnings("unused")
					JsArrayString dest = response.getDestinationAddresses();
					@SuppressWarnings("unused")
					JsArrayString org = response.getOriginAddresses();
					JsArray<DistanceMatrixResponseRow> rows = response.getRows();

					GWT.log("rows.length=" + rows.length());
					DistanceMatrixResponseRow d = rows.get(0);
					DistanceMatrixResponseElement e = d.getElements().get(0);
					String distance = e.getDistance().getText();
					String name = placeResult.getName();
					String gasPrice = "3.70";
					String address = placeResult.getFormatted_Address();
					String phone = placeResult.getFormatted_Phone_Number();
					int rating = placeResult.getRating();
					placeResult.getRating();
					flowPanel.getStore().add(new Place(name, distance, gasPrice, address, phone, rating));
				}

			}
		});

	}

}
