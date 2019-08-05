package main.model.db;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;


public class RS_Converter {

    public static JSONArray convertToJSON(ResultSet resultSet){
        JSONArray jsonArray = new JSONArray();
        try{while (resultSet.next()) {
            int total_columns = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_columns; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
            }
            jsonArray.put(obj);
        }
        }catch (SQLException | JSONException e){
            e.printStackTrace();
        }

        return jsonArray;
    }
}
