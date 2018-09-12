/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sdap.mudrod.metadata.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.sdap.mudrod.driver.ESDriver;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;

public class GuiyangMetadata extends Metadata {

	public GuiyangMetadata() {
		
	}

	public GuiyangMetadata(String shortname) {
		this.shortname = shortname;
	}
	

	public GuiyangMetadata(Map<String, Object> result, ESDriver es, String index) {
		
	}

	@Override
	public List<String> getAllTermList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseBoundingBox(Map<String, Object> result) {
		// TODO Auto-generated method stub
		northLat = 0;
		southLat = 0;
		westLon = 0;
		eastLon = 0;
		
		List<String> bbox = getBbox(result);
		if (bbox.size() > 0) {
			northLat = Double.parseDouble(bbox.get(0));
			southLat = Double.parseDouble(bbox.get(1));
			westLon = Double.parseDouble(bbox.get(2));
			eastLon = Double.parseDouble(bbox.get(3));
		}

	}
	
	  public List<String> getBbox(Map<String, Object> result) {
		  String north = "0";
		  String south = "0";
		  String east = "0";
		  String west = "0";
		  
		  HashMap metadata_Hash = (HashMap) result.get("metadata");
		  HashMap idinfo_Hash = (HashMap) metadata_Hash.get("idinfo");			  
		  HashMap spdom_Hash = (HashMap) idinfo_Hash.get("spdom");
		  HashMap bounding_Hash = (HashMap) spdom_Hash.get("bounding");
		  
		  west = (String) bounding_Hash.get("westbc");
		  east = (String) bounding_Hash.get("eastbc");
		  north = (String) bounding_Hash.get("northbc");
		  south = (String) bounding_Hash.get("southbc");
			  
		  
		  List<String> bbox = new ArrayList<String>();
		  bbox.add(north);
		  bbox.add(south);
		  bbox.add(west);
		  bbox.add(east);
		  
		  return bbox;
	  }
	
}
