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
package org.apache.sdap.mudrod.ssearch.structure;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Customized based on FGDC format for Guiyang project

public class Guiyang extends SResult {
	DecimalFormat NDForm = new DecimalFormat("#.##");
	SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
	final Integer MAX_CHAR = 700;

	public Guiyang(SResult sr) {
		super(sr);
	}

	public Guiyang(SearchHit hit) {
		Map<String, Object> result = hit.getSource();
		System.out.println("You are now in Guiyang version");
		System.out.println(result);
		this.term = Double.valueOf(NDForm.format(hit.getScore()));
		this.shortName = getValue(result, "metadata-idinfo-citation-citeinfo-title");
		this.longName = "";
		this.topic = getValue(result, "metadata-idinfo-keywords-theme-themekey");
		this.description = getValue(result, "metadata-idinfo-descript-abstract");
		this.releaseDate = 0.0;
		this.startDate = "";
		this.endDate = "";

		// string format
		this.processingLevel = "";
		// numeric format
		this.processingL = 0.0;
		this.userPop = 0.0;
		this.allPop = 0.0;
		this.monthPop = 0.0;
		this.sensors = "";
	}

//	public String getDescription(Map<String, Object> result) {
//		String content = "";
//		try {
//			HashMap metadata_Hash = (HashMap) result.get("metadata");
//			HashMap idinfo_Hash = (HashMap) metadata_Hash.get("idinfo");
//			HashMap descript_Hash = (HashMap) idinfo_Hash.get("descript");
//			content = (String) descript_Hash.get("abstract");
//		} catch (Exception e) {
//			content = "";
//		}
//
////    String content = (String) title;
////    if (!"".equals(content)) {
////      int maxLength = (content.length() < MAX_CHAR) ? content.length() : MAX_CHAR;
////      content = content.trim().substring(0, maxLength - 1) + "...";
////    }
//		return content;
//	}
//
//	public String getTitle(Map<String, Object> result) {
//		String title = "";
//		try {
//			HashMap metadata_Hash = (HashMap) result.get("metadata");
//			HashMap idinfo_Hash = (HashMap) metadata_Hash.get("idinfo");
//			HashMap citation_Hash = (HashMap) idinfo_Hash.get("citation");
//			HashMap citeinfo_Hash = (HashMap) citation_Hash.get("citeinfo");
//			title = (String) citeinfo_Hash.get("title");
//		} catch (Exception e) {
//			title = "";
//		}
//
//		return title;
//	}
//
//	public String getTopic(Map<String, Object> result) {
//		HashMap metadata_Hash = (HashMap) result.get("metadata");
//		HashMap idinfo_Hash = (HashMap) metadata_Hash.get("idinfo");
//		HashMap keywords_Hash = (HashMap) idinfo_Hash.get("keywords");
//		HashMap theme_Hash = (HashMap) keywords_Hash.get("theme");
//		String topic = (String) theme_Hash.get("themekey");
//
//		return topic;
//	}
//
//	public double getDate(Map<String, Object> result) {
//		HashMap metadata_Hash = (HashMap) result.get("metadata");
//		HashMap metainfo_Hash = (HashMap) metadata_Hash.get("metainfo");
//		double date = (double) metainfo_Hash.get("metd");
//
//		return date;
//	}

	public String getValue(Map<String, Object> result, String name) {
		String[] names = name.split("-");
		int len = names.length;
		String value = "";
		HashMap metadata_Hash = null;
		HashMap metadata_Hash_temp = null;
		try {
			for (int i = 0; i < len - 1; i++) {
				if (i == 0) {
					metadata_Hash_temp = (HashMap) result.get(names[i]);
					metadata_Hash = metadata_Hash_temp;
				} else {
					metadata_Hash = (HashMap) metadata_Hash.get(names[i]);
				}
			}

			value = (String) metadata_Hash.get(names[len - 1]);
		} catch (Exception e) {
			value = "";
		}

		return value;
	}
}
