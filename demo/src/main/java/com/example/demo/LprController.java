package com.example.demo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.*;
// import com.twoway.gamaniacms.entity.RecordRepository;
// import com.twoway.gamaniacms.handler.SocketTextHandler;
// import com.twoway.gamaniacms.model.RecordSearch;
// import com.twoway.gamaniacms.tool.GateTool;

@CrossOrigin
@RestController
@RequestMapping("/lpr")
public class LprController {


  @Autowired
  RecordRepository recordRepository;

  @GetMapping("/getAllCars")
  public ResponseEntity<List<Record>> getAllUsers(){
      return new ResponseEntity<List<Record>>(((List<Record>) recordRepository.findAll()) , HttpStatus.OK);
  }

  @GetMapping("/getById/{id}")
    public ResponseEntity<Record> getUserByID(@PathVariable("id") int ID){
      return new ResponseEntity<Record>(recordRepository.findById(ID).orElse(null), HttpStatus.OK);
    }

 
  @PostMapping("/event")
  public void lprEventPost(@RequestBody Record record) {

    try{
      System.out.println(record.toString());
      //System.out.println(user.getPlateNumber());
       recordRepository.save(record);
    }
    catch(Exception ex){
        ex.getMessage();
    }


    /* 透過 ws 傳更新資料給前端 */
    for (WebSocketSession session : SocketTextHandler.getSessionList()) {
      try {
        session.sendMessage(
            new TextMessage("update"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @GetMapping("/cams/latest")
  public Map<String, Record> getAllLatestRecordByCam() {
    Map<String, Record> latestCamsMap = new HashMap<>();

    latestCamsMap.put("cam1", latestCamsTool("cam1"));
    //latestCamsMap.put("cam2", latestCamsTool("cam2"));

    return latestCamsMap;
  } 

  public Record latestCamsTool(String cameraId) {
    Optional<Record> opr = recordRepository.findAllLatestRecordByCameraId(cameraId);

    if (opr.isPresent()) {
      return opr.get();
    } else {
      return null;
    }
  }

}
