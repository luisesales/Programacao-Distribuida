package com.kore.Classes;

public class WalEntry {
    private String requestId;
    private String payload;
    private RequestStatus status;

    public WalEntry(String requestId, String payload) {
        this.requestId = requestId;
        this.payload = payload;
        this.status = RequestStatus.PENDING;
    }
    public WalEntry(String requestId, String payload, RequestStatus status) {
        this.requestId = requestId;
        this.payload = payload;
        this.status = status;
    }

    public void setStatus(RequestStatus status){
        this.status = status;
    }

    public RequestStatus getStatus(){
        return status;
    }

    public String getId(){
        return requestId;
    }

    public String getPayload(){
        return payload;
    }
    
    public String getWalEntry(){
        return requestId+" - "+payload+" - "+status.getLabel();
    }

    public static WalEntry getWalEntry(String msg){
        String[] request = msg.split(" - ");
        WalEntry entry = new WalEntry(request[0].trim(), request[1].trim(),RequestStatus.fromLabel(request[2].trim()));
        return entry;
    }
   
}
