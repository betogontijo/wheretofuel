package com.betogontijo.wheretofuel.client;

/*
 * #%L
 * GWT Maps API V3 - Showcase
 * %%
 * Copyright (C) 2011 - 2012 GWT Maps API V3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.events.bounds.BoundsChangeMapEvent;
import com.google.gwt.maps.client.events.bounds.BoundsChangeMapHandler;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.place.PlaceChangeMapEvent;
import com.google.gwt.maps.client.events.place.PlaceChangeMapHandler;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.placeslib.Autocomplete;
import com.google.gwt.maps.client.placeslib.AutocompleteOptions;
import com.google.gwt.maps.client.placeslib.AutocompleteType;
import com.google.gwt.maps.client.placeslib.PlaceGeometry;
import com.google.gwt.maps.client.placeslib.PlaceResult;
import com.google.gwt.maps.client.services.DirectionsRenderer;
import com.google.gwt.maps.client.services.DirectionsRendererOptions;
import com.google.gwt.maps.client.services.DirectionsRequest;
import com.google.gwt.maps.client.services.DirectionsResult;
import com.google.gwt.maps.client.services.DirectionsResultHandler;
import com.google.gwt.maps.client.services.DirectionsService;
import com.google.gwt.maps.client.services.DirectionsStatus;
import com.google.gwt.maps.client.services.Distance;
import com.google.gwt.maps.client.services.DistanceMatrixElementStatus;
import com.google.gwt.maps.client.services.DistanceMatrixRequest;
import com.google.gwt.maps.client.services.DistanceMatrixRequestHandler;
import com.google.gwt.maps.client.services.DistanceMatrixResponse;
import com.google.gwt.maps.client.services.DistanceMatrixResponseElement;
import com.google.gwt.maps.client.services.DistanceMatrixResponseRow;
import com.google.gwt.maps.client.services.DistanceMatrixService;
import com.google.gwt.maps.client.services.DistanceMatrixStatus;
import com.google.gwt.maps.client.services.Duration;
import com.google.gwt.maps.client.services.TravelMode;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * See <a href=
 * "https://developers.google.com/maps/documentation/javascript/layers.html#FusionTables"
 * >FusionTables API Doc</a>
 */
public class DirectionsServiceMapWidget extends Composite {

	private VerticalPanel pWidget;
	private MapWidget mapWidget;
	private HTML htmlDistanceMatrixService = new HTML("&nbsp;");
	private TextBox tbPlaces;
	private LatLng currentPos = null;

	public DirectionsServiceMapWidget() {
		pWidget = new VerticalPanel();
		initWidget(pWidget);

		draw();
	}

	private void draw() {
		pWidget.clear();
		pWidget.add(new HTML("<br/>"));

		tbPlaces = new TextBox();
		tbPlaces.setWidth("350px");

		HorizontalPanel hp = new HorizontalPanel();
		pWidget.add(hp);
		hp.add(tbPlaces);
		hp.add(htmlDistanceMatrixService);

		drawMap();
		// drawAutoComplete();
		// drawDirectionsWithMidPoint();
	}

	private void drawMap() {
		Geolocation geolocation = Geolocation.getIfSupported();
		geolocation.getCurrentPosition(new Callback<Position, PositionError>() {

			@Override
			public void onSuccess(Position result) {
				// TODO Auto-generated method stub
				drawMap(result.getCoordinates().getLatitude(), result.getCoordinates().getLongitude());
			}

			@Override
			public void onFailure(PositionError reason) {
				// TODO Auto-generated method stub
				drawMap(-19.9283827, -43.9924392);
			}
		});
	}

	Marker markerBasic;

	final DirectionsRenderer directionsDisplay = DirectionsRenderer
			.newInstance(DirectionsRendererOptions.newInstance());

	private void drawMap(double latitude, double longitude) {
		currentPos = LatLng.newInstance(latitude, longitude);
		MapOptions opts = MapOptions.newInstance();
		opts.setZoom(13);
		opts.setCenter(currentPos);
		opts.setMapTypeId(MapTypeId.ROADMAP);

		mapWidget = new MapWidget(opts);

		directionsDisplay.setMap(mapWidget);
		DirectionsRendererOptions directionsRendererOptions = DirectionsRendererOptions.newInstance();
		directionsRendererOptions.setDraggable(true);
		directionsDisplay.setOptions(directionsRendererOptions);
		MarkerOptions options = MarkerOptions.newInstance();
		options.setPosition(currentPos);
		markerBasic = Marker.newInstance(options);
		markerBasic.setMap(mapWidget);

		pWidget.add(mapWidget);
		mapWidget.setSize(RootPanel.getBodyElement().getClientWidth() + "px", "550px");

		mapWidget.addClickHandler(new ClickMapHandler() {
			public void onEvent(ClickMapEvent event) {
				// TODO fix the event getting, getting ....
				GWT.log("clicked on latlng=" + event.getMouseEvent().getLatLng());
				drawDirectionsWithMidPoint(currentPos, event.getMouseEvent().getLatLng());
			}
		});

		drawAutoComplete();
	}

	private void drawAutoComplete() {

		Element element = tbPlaces.getElement();

		AutocompleteType[] types = new AutocompleteType[2];
		types[0] = AutocompleteType.ESTABLISHMENT;
		types[1] = AutocompleteType.GEOCODE;

		AutocompleteOptions options = AutocompleteOptions.newInstance();
		options.setTypes(types);
		options.setBounds(mapWidget.getBounds());

		final Autocomplete autoComplete = Autocomplete.newInstance(element, options);

		autoComplete.addPlaceChangeHandler(new PlaceChangeMapHandler() {
			public void onEvent(PlaceChangeMapEvent event) {

				PlaceResult result = autoComplete.getPlace();
				GWT.log(result.getTypes().join(","));

				PlaceGeometry geomtry = result.getGeometry();
				LatLng center = geomtry.getLocation();

				drawDirectionsWithMidPoint(currentPos, center);

				mapWidget.panTo(center);

				GWT.log("place changed center=" + center);
			}
		});

		mapWidget.addBoundsChangeHandler(new BoundsChangeMapHandler() {
			public void onEvent(BoundsChangeMapEvent event) {
				LatLngBounds bounds = mapWidget.getBounds();
				autoComplete.setBounds(bounds);
			}
		});
	}

	private void drawDirectionsWithMidPoint(LatLng origin, LatLng destination) {
		DirectionsRequest request = DirectionsRequest.newInstance();
		request.setOrigin(origin);
		request.setDestination(destination);
		request.setTravelMode(TravelMode.DRIVING);
		request.setOptimizeWaypoints(true);

		DirectionsService o = DirectionsService.newInstance();
		o.route(request, new DirectionsResultHandler() {
			public void onCallback(DirectionsResult result, DirectionsStatus status) {
				if (status == DirectionsStatus.OK) {
					markerBasic.clear();
					directionsDisplay.setDirections(result);
					getDistance(origin, destination);
				} else if (status == DirectionsStatus.INVALID_REQUEST) {

				} else if (status == DirectionsStatus.MAX_WAYPOINTS_EXCEEDED) {

				} else if (status == DirectionsStatus.NOT_FOUND) {

				} else if (status == DirectionsStatus.OVER_QUERY_LIMIT) {

				} else if (status == DirectionsStatus.REQUEST_DENIED) {

				} else if (status == DirectionsStatus.UNKNOWN_ERROR) {

				} else if (status == DirectionsStatus.ZERO_RESULTS) {

				}

			}
		});
	}

	private void getDistance(LatLng origin, LatLng destination) {

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

				if (status == DistanceMatrixStatus.INVALID_REQUEST) {

				} else if (status == DistanceMatrixStatus.MAX_DIMENSIONS_EXCEEDED) {

				} else if (status == DistanceMatrixStatus.MAX_ELEMENTS_EXCEEDED) {

				} else if (status == DistanceMatrixStatus.OK) {

					@SuppressWarnings("unused")
					JsArrayString dest = response.getDestinationAddresses();
					@SuppressWarnings("unused")
					JsArrayString org = response.getOriginAddresses();
					JsArray<DistanceMatrixResponseRow> rows = response.getRows();

					GWT.log("rows.length=" + rows.length());
					DistanceMatrixResponseRow d = rows.get(0);
					JsArray<DistanceMatrixResponseElement> elements = d.getElements();
					for (int i = 0; i < elements.length(); i++) {
						DistanceMatrixResponseElement e = elements.get(i);
						Distance distance = e.getDistance();
						Duration duration = e.getDuration();

						@SuppressWarnings("unused")
						DistanceMatrixElementStatus st = e.getStatus();
						GWT.log("distance=" + distance.getText() + " value=" + distance.getValue());
						GWT.log("duration=" + duration.getText() + " value=" + duration.getValue());

						String html = "&nbsp;&nbsp;Distance=" + distance.getText() + " Duration=" + duration.getText()
								+ " ";
						htmlDistanceMatrixService.setHTML(html);
					}

				} else if (status == DistanceMatrixStatus.OVER_QUERY_LIMIT) {

				} else if (status == DistanceMatrixStatus.REQUEST_DENIED) {

				} else if (status == DistanceMatrixStatus.UNKNOWN_ERROR) {

				}

			}
		});

	}

}
