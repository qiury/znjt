// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Transfer.proto

package com.znjt.proto;

public final class TransferProto {
  private TransferProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_SyncMulImgResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_SyncMulImgResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_SyncMulImgRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_SyncMulImgRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_SyncMulSingleImgResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_SyncMulSingleImgResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_SyncMulSingleImgRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_SyncMulSingleImgRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_SyncDataRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_SyncDataRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_SyncDataResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_SyncDataResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_INIRecord_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_INIRecord_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_GPSRecord_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_GPSRecord_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_znjt_proto_GPSSingleRecord_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_znjt_proto_GPSSingleRecord_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016Transfer.proto\022\016com.znjt.proto\"p\n\022Sync" +
      "MulImgResponse\022+\n\tdata_type\030\001 \001(\0162\030.com." +
      "znjt.proto.DataType\022-\n\ngps_record\030\003 \003(\0132" +
      "\031.com.znjt.proto.GPSRecord\"o\n\021SyncMulImg" +
      "Request\022+\n\tdata_type\030\001 \001(\0162\030.com.znjt.pr" +
      "oto.DataType\022-\n\ngps_record\030\003 \003(\0132\031.com.z" +
      "njt.proto.GPSRecord\"\203\001\n\030SyncMulSingleImg" +
      "Response\022+\n\tdata_type\030\001 \001(\0162\030.com.znjt.p" +
      "roto.DataType\022:\n\021gps_single_record\030\002 \003(\013" +
      "2\037.com.znjt.proto.GPSSingleRecord\"\202\001\n\027Sy" +
      "ncMulSingleImgRequest\022+\n\tdata_type\030\001 \001(\016" +
      "2\030.com.znjt.proto.DataType\022:\n\021gps_single" +
      "_record\030\002 \003(\0132\037.com.znjt.proto.GPSSingle" +
      "Record\"\252\001\n\017SyncDataRequest\022+\n\tdata_type\030" +
      "\001 \001(\0162\030.com.znjt.proto.DataType\022/\n\nini_r" +
      "ecord\030\002 \001(\0132\031.com.znjt.proto.INIRecordH\000" +
      "\022/\n\ngps_record\030\003 \001(\0132\031.com.znjt.proto.GP" +
      "SRecordH\000B\010\n\006record\"\253\001\n\020SyncDataResponse" +
      "\022+\n\tdata_type\030\001 \001(\0162\030.com.znjt.proto.Dat" +
      "aType\022/\n\nini_record\030\002 \001(\0132\031.com.znjt.pro" +
      "to.INIRecordH\000\022/\n\ngps_record\030\003 \001(\0132\031.com" +
      ".znjt.proto.GPSRecordH\000B\010\n\006record\"K\n\tINI" +
      "Record\022\030\n\020client_record_id\030\001 \001(\t\022\016\n\006data" +
      "Id\030\002 \001(\t\022\024\n\014serv_ops_res\030\003 \001(\010\"\230\001\n\tGPSRe" +
      "cord\022\030\n\020client_record_id\030\001 \001(\t\022\016\n\006dataId" +
      "\030\002 \001(\t\022\024\n\014serv_ops_res\030\003 \001(\010\022\020\n\010img_data" +
      "\030\004 \003(\014\022\020\n\010file_err\030\005 \001(\010\022\023\n\013losted_size\030" +
      "\006 \001(\005\022\022\n\nfile_names\030\007 \003(\t\"\210\001\n\017GPSSingleR" +
      "ecord\022\030\n\020client_record_id\030\001 \001(\t\022\016\n\006dataI" +
      "d\030\002 \001(\t\022\024\n\014serv_ops_res\030\003 \001(\010\022\020\n\010img_dat" +
      "a\030\004 \001(\014\022\020\n\010file_err\030\005 \001(\010\022\021\n\tfile_name\030\006" +
      " \001(\t*I\n\010DataType\022\t\n\005T_INI\020\000\022\t\n\005T_GPS\020\001\022\020" +
      "\n\014T_GPS_SINGLE\020\002\022\025\n\021T_GPS_SINGLE_TEST\020\0032" +
      "\375\003\n\017TransferService\022^\n\023transporterByStre" +
      "am\022\037.com.znjt.proto.SyncDataRequest\032 .co" +
      "m.znjt.proto.SyncDataResponse\"\000(\0010\001\022X\n\021t" +
      "ransporterBySync\022\037.com.znjt.proto.SyncDa" +
      "taRequest\032 .com.znjt.proto.SyncDataRespo" +
      "nse\"\000\022\\\n\025transporterBySyncTest\022\037.com.znj" +
      "t.proto.SyncDataRequest\032 .com.znjt.proto" +
      ".SyncDataResponse\"\000\022_\n\024transporterMulByS" +
      "ync\022!.com.znjt.proto.SyncMulImgRequest\032\"" +
      ".com.znjt.proto.SyncMulImgResponse\"\000\022q\n\032" +
      "transporterMulSingleBySync\022\'.com.znjt.pr" +
      "oto.SyncMulSingleImgRequest\032(.com.znjt.p" +
      "roto.SyncMulSingleImgResponse\"\000B#\n\016com.z" +
      "njt.protoB\rTransferProtoH\001P\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_znjt_proto_SyncMulImgResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_znjt_proto_SyncMulImgResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_SyncMulImgResponse_descriptor,
        new java.lang.String[] { "DataType", "GpsRecord", });
    internal_static_com_znjt_proto_SyncMulImgRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_znjt_proto_SyncMulImgRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_SyncMulImgRequest_descriptor,
        new java.lang.String[] { "DataType", "GpsRecord", });
    internal_static_com_znjt_proto_SyncMulSingleImgResponse_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_com_znjt_proto_SyncMulSingleImgResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_SyncMulSingleImgResponse_descriptor,
        new java.lang.String[] { "DataType", "GpsSingleRecord", });
    internal_static_com_znjt_proto_SyncMulSingleImgRequest_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_com_znjt_proto_SyncMulSingleImgRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_SyncMulSingleImgRequest_descriptor,
        new java.lang.String[] { "DataType", "GpsSingleRecord", });
    internal_static_com_znjt_proto_SyncDataRequest_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_com_znjt_proto_SyncDataRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_SyncDataRequest_descriptor,
        new java.lang.String[] { "DataType", "IniRecord", "GpsRecord", "Record", });
    internal_static_com_znjt_proto_SyncDataResponse_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_com_znjt_proto_SyncDataResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_SyncDataResponse_descriptor,
        new java.lang.String[] { "DataType", "IniRecord", "GpsRecord", "Record", });
    internal_static_com_znjt_proto_INIRecord_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_com_znjt_proto_INIRecord_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_INIRecord_descriptor,
        new java.lang.String[] { "ClientRecordId", "DataId", "ServOpsRes", });
    internal_static_com_znjt_proto_GPSRecord_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_com_znjt_proto_GPSRecord_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_GPSRecord_descriptor,
        new java.lang.String[] { "ClientRecordId", "DataId", "ServOpsRes", "ImgData", "FileErr", "LostedSize", "FileNames", });
    internal_static_com_znjt_proto_GPSSingleRecord_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_com_znjt_proto_GPSSingleRecord_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_znjt_proto_GPSSingleRecord_descriptor,
        new java.lang.String[] { "ClientRecordId", "DataId", "ServOpsRes", "ImgData", "FileErr", "FileName", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
