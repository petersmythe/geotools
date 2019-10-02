/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2017, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.ows.wmts;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.geotools.data.ResourceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.HTTPClient;
import org.geotools.data.ows.OperationType;
import org.geotools.data.ows.SimpleHttpClient;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.CRSEnvelope;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.request.GetFeatureInfoRequest;
import org.geotools.ows.wms.response.GetFeatureInfoResponse;
import org.geotools.ows.wmts.model.WMTSCapabilities;
import org.geotools.ows.wmts.model.WMTSServiceType;
import org.geotools.ows.wmts.request.GetTileRequest;
import org.geotools.referencing.CRS;
import org.geotools.tile.Tile;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * WebMapTileServer is a class representing a WMTS.
 *
 * <p>It is used to access the Capabilities document and perform requests. It will perform version
 * negotiation automatically and use the highest known version that the server can communicate.
 *
 * @author ian
 * @author Emanuele Tajariol (etj at geo-solutions dot it)
 */
public class WebMapTileServer extends AbstractOpenWebService<WMTSCapabilities, Layer> {

    private WMTSServiceType type;

    private final Map<String, String> headers = new HashMap<>();

    /**
     * @param serverURL
     * @param httpClient
     * @param capabilities
     * @param hints
     * @throws ServiceException
     * @throws IOException
     */
    public WebMapTileServer(
            URL serverURL,
            HTTPClient httpClient,
            WMTSCapabilities capabilities,
            Map<String, Object> hints)
            throws ServiceException, IOException {
        super(serverURL, httpClient, capabilities, hints);
        setType(capabilities.getType());
    }

    /**
     * @param serverURL
     * @param httpClient
     * @param capabilities
     * @throws ServiceException
     * @throws IOException
     */
    public WebMapTileServer(URL serverURL, HTTPClient httpClient, WMTSCapabilities capabilities)
            throws ServiceException, IOException {
        super(serverURL, httpClient, capabilities);
        setType(super.capabilities.getType());
    }

    /**
     * @param serverURL
     * @throws IOException
     * @throws ServiceException
     */
    public WebMapTileServer(URL serverURL) throws IOException, ServiceException {
        super(serverURL);
        setType(capabilities.getType());
    }

    /**
     * @param capabilities
     * @throws IOException
     * @throws ServiceException
     */
    public WebMapTileServer(WMTSCapabilities capabilities) throws ServiceException, IOException {
        super(
                capabilities.getRequest().getGetCapabilities().getGet(),
                new SimpleHttpClient(),
                capabilities);
        setType(capabilities.getType());
    }

    /**
     * @param delegate
     * @throws IOException
     * @throws ServiceException
     */
    public WebMapTileServer(WebMapTileServer delegate) throws ServiceException, IOException {
        this(delegate.serverURL);
    }

    @Override
    public WMTSCapabilities getCapabilities() {

        return capabilities;
    }

    @Override
    protected ServiceInfo createInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ResourceInfo createInfo(Layer resource) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setupSpecifications() {
        specs = new WMTSSpecification[1];
        specs[0] = new WMTSSpecification();
    }

    /**
     * @param tileRequest
     * @return
     */
    public Set<Tile> issueRequest(GetTileRequest tileRequest) throws ServiceException {

        return tileRequest.getTiles();
    }

    private void setHttpClientCredentials(HttpClient client) {
        client.getState().clearCredentials();
        if (this.getHTTPClient().getUser() != null && this.getHTTPClient().getPassword() != null) {
            AuthScope authscope = AuthScope.ANY;
            Credentials credentials =
                    new UsernamePasswordCredentials(
                            this.getHTTPClient().getUser(), this.getHTTPClient().getPassword());

            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(authscope, credentials);
        } else {
            client.getParams().setAuthenticationPreemptive(false);
        }
    }

    /** @return */
    public GetTileRequest createGetTileRequest() {

        URL url;

        if (WMTSServiceType.KVP.equals(type)) {
            url = findURL(getCapabilities().getRequest().getGetTile());
        } else {
            url = serverURL;
        }

        HttpClient client = this.getHttpClient();
        setHttpClientCredentials(client);

        GetTileRequest request =
                ((WMTSSpecification) specification)
                        .createGetTileRequest(url, (Properties) null, capabilities, client);

        request.getHeaders().putAll(headers);

        return request;
    }

    protected HttpClient getHttpClient() {
        return new HttpClient();
    }

    private URL findURL(OperationType operation) {
        if (WMTSServiceType.KVP.equals(type)) {
            if (operation.getGet() != null) {
                return operation.getGet();
            }
            return serverURL;

        } else {
            return null;
        }
    }

    /**
     * @param getmap
     * @return
     */
    public GetFeatureInfoRequest createGetFeatureInfoRequest(GetTileRequest getmap) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param request
     * @return
     */
    public GetFeatureInfoResponse issueRequest(GetFeatureInfoRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @param type */
    public void setType(WMTSServiceType type) {
        this.type = type;
    }

    public WMTSServiceType getType() {
        return type;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param layer
     * @param crs
     * @return
     */
    public GeneralEnvelope getEnvelope(Layer layer, CoordinateReferenceSystem crs) {
        Map<String, CRSEnvelope> boundingBoxes = layer.getBoundingBoxes();
        CRSEnvelope box = boundingBoxes.get(crs.getName().getCode());
        if (box != null) {
            return new GeneralEnvelope(box);
        }
        for (String key : boundingBoxes.keySet()) {
            box = boundingBoxes.get(key);
            if (CRS.equalsIgnoreMetadata(crs, box.getCoordinateReferenceSystem())) {
                return new GeneralEnvelope(box);
            }
        }
        return null;
    }
}
