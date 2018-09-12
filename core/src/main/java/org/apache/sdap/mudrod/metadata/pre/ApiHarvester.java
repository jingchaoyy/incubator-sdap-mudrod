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
package org.apache.sdap.mudrod.metadata.pre;

import com.google.gson.JsonArray;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Coordinate;

import org.apache.commons.io.IOUtils;
import org.apache.sdap.mudrod.discoveryengine.DiscoveryStepAbstract;
import org.apache.sdap.mudrod.driver.ESDriver;
import org.apache.sdap.mudrod.driver.SparkDriver;
import org.apache.sdap.mudrod.main.MudrodConstants;
import org.apache.sdap.mudrod.metadata.structure.GuiyangMetadata;
import org.apache.sdap.mudrod.metadata.structure.Metadata;
import org.apache.sdap.mudrod.metadata.structure.PODAACMetadata;
import org.apache.sdap.mudrod.ssearch.structure.PlanetDefense;
import org.apache.sdap.mudrod.ssearch.structure.Podaac;
import org.apache.sdap.mudrod.utils.HttpRequest;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.geo.builders.EnvelopeBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * ClassName: ApiHarvester Function: Harvest metadata from PO.DAACweb service.
 */
public class ApiHarvester extends DiscoveryStepAbstract {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(ApiHarvester.class);
  protected static final String METADATA_MAPPINGS = "metadata_mapping.json";
  /**
   * Creates a new instance of ApiHarvester.
   *
   * @param props the Mudrod configuration
   * @param es    the Elasticsearch drive
   * @param spark the spark driver
   */
  public ApiHarvester(Properties props, ESDriver es, SparkDriver spark) {
    super(props, es, spark);
  }

  @Override
  public Object execute() {
    LOG.info("Starting Metadata harvesting.");
    startTime = System.currentTimeMillis();
    //remove old metadata from ES
    es.deleteType(props.getProperty(MudrodConstants.ES_INDEX_NAME), props.getProperty(MudrodConstants.RAW_METADATA_TYPE));
    //harvest new metadata using PO.DAAC web services
    if("1".equals(props.getProperty(MudrodConstants.METADATA_DOWNLOAD))) 
      harvestMetadatafromWeb();
    es.createBulkProcessor();
    addMetadataMapping();
    importToES();
    es.destroyBulkProcessor();
    System.out.println("Finish add metadata");
    
    es.createBulkProcessor();
	try {
		addGeoEnvelope();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("Finish add spatial range!!!");
    es.destroyBulkProcessor();
    endTime = System.currentTimeMillis();
    es.refreshIndex();
    LOG.info("Metadata harvesting completed. Time elapsed: {}", (endTime - startTime) / 1000);
    return null;
  }

  /**
   * addMetadataMapping: Add mapping to index metadata in Elasticsearch. Please
   * invoke this method before import metadata to Elasticsearch.
   */
  public void addMetadataMapping() {

		InputStream mappingsStream = getClass().getClassLoader().getResourceAsStream(METADATA_MAPPINGS);
		JSONObject mappingJSON = null;
		try {
			mappingJSON = new JSONObject(IOUtils.toString(mappingsStream));
		} catch (JSONException | IOException e1) {
			LOG.error("Error reading Elasticsearch mappings!", e1);
		}

		es.getClient().admin().indices().preparePutMapping(props.getProperty(MudrodConstants.ES_INDEX_NAME))
				.setType(props.getProperty(MudrodConstants.RAW_METADATA_TYPE)).setSource(mappingJSON.toString())
				.execute().actionGet();
	}

  /**
   * importToES: Index metadata into elasticsearch from local file directory.
   * Please make sure metadata have been harvest from web service before
   * invoking this method.
   */
  private void importToES() {
    File directory = new File(props.getProperty(MudrodConstants.RAW_METADATA_PATH));
    if(!directory.exists())
      directory.mkdir();
    File[] fList = directory.listFiles();
    for (File file : fList) {
      InputStream is;
      try {
        is = new FileInputStream(file);
        importSingleFileToES(is);
      } catch (FileNotFoundException e) {
        LOG.error("Error finding file!", e);
      }

    }
  }

  private void importSingleFileToES(InputStream is) {
    try {
      String jsonTxt = IOUtils.toString(is);
      JsonParser parser = new JsonParser();
      JsonElement item = parser.parse(jsonTxt);
      IndexRequest ir = new IndexRequest(props.getProperty(MudrodConstants.ES_INDEX_NAME), props.getProperty(MudrodConstants.RAW_METADATA_TYPE)).source(item.toString());
      es.getBulkProcessor().add(ir);
    } catch (IOException e) {
      LOG.error("Error indexing metadata record!", e);
    }
  }
  
  private void addGeoEnvelope() throws IOException {
	  	es.refreshIndex();
		String index = props.getProperty(MudrodConstants.ES_INDEX_NAME);
		String type = props.getProperty(MudrodConstants.RAW_METADATA_TYPE);
		SearchResponse scrollResp = es.getClient().prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery()).setScroll(new TimeValue(60000)).setSize(100).execute()
				.actionGet();

		while (true) {
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				Map<String, Object> result = hit.getSource();
				String id =  hit.getId();
				EnvelopeBuilder envBuilder  = ShapeBuilders.newEnvelope(new Coordinate(0, 0), new Coordinate(0, 0)); //default
				String format = props.getProperty(MudrodConstants.RANKING_META_FORMAT);
				Metadata metadata = null;
				if(MudrodConstants.PODAAC_META_FORMAT.equals(format)){
					metadata = new PODAACMetadata(result, es, index);
				    
				}
				else if(MudrodConstants.GUIYANG_META_FORMAT.equals(format)){
					metadata = new GuiyangMetadata(result, es, index);
				    
				}
				
				if(null != metadata) {
					metadata.parseBoundingBox(result);
					envBuilder  = metadata.getBoundingBox();
				}
  	
				UpdateRequest ur = new UpdateRequest(index, type, id).doc(jsonBuilder().startObject().field("metedataspatialcoverage", envBuilder).endObject());
				es.getBulkProcessor().add(ur);
			}
			
			scrollResp = es.getClient().prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000))
					.execute().actionGet();
			if (scrollResp.getHits().getHits().length == 0) {
				break;
			}
		}
	}

  /**
   * harvestMetadatafromWeb: Harvest metadata from PO.DAAC web service.
   */
  private void harvestMetadatafromWeb() {
    LOG.info("Metadata download started.");
    int startIndex = 0;
    int doc_length = 0;
    JsonParser parser = new JsonParser();
    do {
      String searchAPI = props.getProperty(MudrodConstants.METADATA_DOWNLOAD_URL);
      searchAPI = searchAPI.replace("$startIndex", Integer.toString(startIndex));
      HttpRequest http = new HttpRequest();
      String response = http.getRequest(searchAPI);

      JsonElement json = parser.parse(response);
      JsonObject responseObject = json.getAsJsonObject();
      JsonArray docs = responseObject.getAsJsonObject("response").getAsJsonArray("docs");

      doc_length = docs.size();

      File file = new File(props.getProperty(MudrodConstants.RAW_METADATA_PATH));
      if (!file.exists()) {
        if (file.mkdir()) {
          LOG.info("Directory is created!");
        } else {
          LOG.error("Failed to create directory!");
        }
      }
      for (int i = 0; i < doc_length; i++) {
        JsonElement item = docs.get(i);
        int docId = startIndex + i;
        File itemfile = new File(props.getProperty(MudrodConstants.RAW_METADATA_PATH) + "/" + docId + ".json");

        try (FileWriter fw = new FileWriter(itemfile.getAbsoluteFile()); BufferedWriter bw = new BufferedWriter(fw)) {
          itemfile.createNewFile();
          bw.write(item.toString());
        } catch (IOException e) {
          LOG.error("Error writing metadata to local file!", e);
        }
      }

      startIndex += 10;

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        LOG.error("Error entering Elasticsearch Mappings!", e);
        Thread.currentThread().interrupt();
      }

    } while (doc_length != 0);
    
    LOG.info("Metadata downloading finished");
  }

  @Override
  public Object execute(Object o) {
    return null;
  }
}
