/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.znjt.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-03-27")
public class ThriftMessageResponse implements org.apache.thrift.TBase<ThriftMessageResponse, ThriftMessageResponse._Fields>, java.io.Serializable, Cloneable, Comparable<ThriftMessageResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ThriftMessageResponse");

  private static final org.apache.thrift.protocol.TField MESSAGES_FIELD_DESC = new org.apache.thrift.protocol.TField("messages", org.apache.thrift.protocol.TType.LIST, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ThriftMessageResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ThriftMessageResponseTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.util.List<GPSImgRecord> messages; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGES((short)1, "messages");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // MESSAGES
          return MESSAGES;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MESSAGES, new org.apache.thrift.meta_data.FieldMetaData("messages", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT            , "GPSImgRecord"))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ThriftMessageResponse.class, metaDataMap);
  }

  public ThriftMessageResponse() {
  }

  public ThriftMessageResponse(
    java.util.List<GPSImgRecord> messages)
  {
    this();
    this.messages = messages;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ThriftMessageResponse(ThriftMessageResponse other) {
    if (other.isSetMessages()) {
      java.util.List<GPSImgRecord> __this__messages = new java.util.ArrayList<GPSImgRecord>(other.messages.size());
      for (GPSImgRecord other_element : other.messages) {
        __this__messages.add(new GPSImgRecord(other_element));
      }
      this.messages = __this__messages;
    }
  }

  public ThriftMessageResponse deepCopy() {
    return new ThriftMessageResponse(this);
  }

  @Override
  public void clear() {
    this.messages = null;
  }

  public int getMessagesSize() {
    return (this.messages == null) ? 0 : this.messages.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<GPSImgRecord> getMessagesIterator() {
    return (this.messages == null) ? null : this.messages.iterator();
  }

  public void addToMessages(GPSImgRecord elem) {
    if (this.messages == null) {
      this.messages = new java.util.ArrayList<GPSImgRecord>();
    }
    this.messages.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<GPSImgRecord> getMessages() {
    return this.messages;
  }

  public ThriftMessageResponse setMessages(@org.apache.thrift.annotation.Nullable java.util.List<GPSImgRecord> messages) {
    this.messages = messages;
    return this;
  }

  public void unsetMessages() {
    this.messages = null;
  }

  /** Returns true if field messages is set (has been assigned a value) and false otherwise */
  public boolean isSetMessages() {
    return this.messages != null;
  }

  public void setMessagesIsSet(boolean value) {
    if (!value) {
      this.messages = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
    switch (field) {
    case MESSAGES:
      if (value == null) {
        unsetMessages();
      } else {
        setMessages((java.util.List<GPSImgRecord>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGES:
      return getMessages();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MESSAGES:
      return isSetMessages();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ThriftMessageResponse)
      return this.equals((ThriftMessageResponse)that);
    return false;
  }

  public boolean equals(ThriftMessageResponse that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_messages = true && this.isSetMessages();
    boolean that_present_messages = true && that.isSetMessages();
    if (this_present_messages || that_present_messages) {
      if (!(this_present_messages && that_present_messages))
        return false;
      if (!this.messages.equals(that.messages))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetMessages()) ? 131071 : 524287);
    if (isSetMessages())
      hashCode = hashCode * 8191 + messages.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(ThriftMessageResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetMessages()).compareTo(other.isSetMessages());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessages()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messages, other.messages);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ThriftMessageResponse(");
    boolean first = true;

    sb.append("messages:");
    if (this.messages == null) {
      sb.append("null");
    } else {
      sb.append(this.messages);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (messages == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'messages' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ThriftMessageResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ThriftMessageResponseStandardScheme getScheme() {
      return new ThriftMessageResponseStandardScheme();
    }
  }

  private static class ThriftMessageResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<ThriftMessageResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ThriftMessageResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MESSAGES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.messages = new java.util.ArrayList<GPSImgRecord>(_list8.size);
                @org.apache.thrift.annotation.Nullable GPSImgRecord _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new GPSImgRecord();
                  _elem9.read(iprot);
                  struct.messages.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setMessagesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ThriftMessageResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.messages != null) {
        oprot.writeFieldBegin(MESSAGES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.messages.size()));
          for (GPSImgRecord _iter11 : struct.messages)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ThriftMessageResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ThriftMessageResponseTupleScheme getScheme() {
      return new ThriftMessageResponseTupleScheme();
    }
  }

  private static class ThriftMessageResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<ThriftMessageResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ThriftMessageResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.messages.size());
        for (GPSImgRecord _iter12 : struct.messages)
        {
          _iter12.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ThriftMessageResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.messages = new java.util.ArrayList<GPSImgRecord>(_list13.size);
        @org.apache.thrift.annotation.Nullable GPSImgRecord _elem14;
        for (int _i15 = 0; _i15 < _list13.size; ++_i15)
        {
          _elem14 = new GPSImgRecord();
          _elem14.read(iprot);
          struct.messages.add(_elem14);
        }
      }
      struct.setMessagesIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

