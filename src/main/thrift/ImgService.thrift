namespace java com.znjt.thrift

struct ThriftMessageRequest{
	1: required list<GPSImgRecord> messages;
}

struct ThriftMessageResponse{
	1: required list<GPSImgRecord> messages;
}


struct GPSImgRecord{
    1: string client_record_id,
    2: string dataId,
    3: bool serv_ops_res,
    4: list<binary> img_datas,
    5: bool file_err,
    6: i32 losted_size;
}

service GPSImgService{
	ThriftMessageResponse transferGPSImgs(1:required ThriftMessageRequest request);
	string ping()
}