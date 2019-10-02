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
import java.util.Properties;
import org.apache.commons.httpclient.HttpClient;
import org.geotools.data.ows.AbstractGetCapabilitiesRequest;
import org.geotools.data.ows.GetCapabilitiesRequest;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.Response;
import org.geotools.data.ows.Specification;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wmts.model.WMTSCapabilities;
import org.geotools.ows.wmts.model.WMTSServiceType;
import org.geotools.ows.wmts.request.AbstractGetTileRequest;
import org.geotools.ows.wmts.response.GetTileResponse;
import org.geotools.ows.wmts.response.WMTSGetCapabilitiesResponse;

/**
 * @author ian
 * @author Emanuele Tajariol (etj at geo-solutions dot it)
 */
public class WMTSSpecification extends Specification {

    public static final String WMTS_VERSION = "1.0.0";

    private WMTSServiceType type;

    /** */
    public WMTSSpecification() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getVersion() {
        //
        return WMTS_VERSION;
    }

    @Override
    public GetCapabilitiesRequest createGetCapabilitiesRequest(URL server) {
        // TODO Auto-generated method stub
        return new GetCapsRequest(server);
    }

    public GetTileRequest createGetTileRequest(
            URL server, Properties props, WMTSCapabilities caps) {
        return this.createGetTileRequest(server, props, caps, new HttpClient());
    }

    public GetTileRequest createGetTileRequest(
            URL server, Properties props, WMTSCapabilities caps, HttpClient client) {
        return new GetTileRequest(server, props, caps, client);
    }

    public static class GetTileRequest extends AbstractGetTileRequest {

        /**
         * @param onlineResource
         * @param properties
         * @param capabilities
         */
        public GetTileRequest(
                URL onlineResource, Properties properties, WMTSCapabilities capabilities) {
            this(onlineResource, properties, capabilities, new HttpClient());
        }

        public GetTileRequest(
                URL onlineResource,
                Properties properties,
                WMTSCapabilities capabilities,
                HttpClient client) {
            super(onlineResource, properties, client);
            this.type = capabilities.getType();
            this.capabilities = capabilities;
        }

        @Override
        public Response createResponse(HTTPResponse response) throws ServiceException, IOException {
            // TODO Auto-generated method stub
            return new GetTileResponse(response, getType());
        }

        @Override
        protected void initVersion() {
            setProperty(VERSION, WMTS_VERSION);
        }

        /** @return the type */
        public WMTSServiceType getType() {
            return type;
        }

        /** @param type the type to set */
        public void setType(WMTSServiceType type) {
            this.type = type;
        }
    }

    public static class GetCapsRequest extends AbstractGetCapabilitiesRequest {
        /**
         * Construct a Request compatible with a 1.0.1 WMTS.
         *
         * @param urlGetCapabilities URL of GetCapabilities document.
         */
        public GetCapsRequest(URL urlGetCapabilities) {
            super(urlGetCapabilities);
        }

        @Override
        protected void initService() {
            setProperty(SERVICE, "WMTS");
        }

        @Override
        protected void initVersion() {
            setProperty(VERSION, WMTS_VERSION); // $NON-NLS-1$ //$NON-NLS-2$
        }

        @Override
        protected String processKey(String key) {
            return WMTSSpecification.processKey(key);
        }

        @Override
        public WMTSGetCapabilitiesResponse createResponse(HTTPResponse httpResponse)
                throws ServiceException, IOException {
            return new WMTSGetCapabilitiesResponse(httpResponse, hints);
        }
    }

    /**
     * @param key
     * @return
     */
    public static String processKey(String key) {

        return key.trim().toUpperCase();
    }
}
