// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: creds.proto

package net.nyhm.driveup.proto;

/**
 * Protobuf type {@code net.nyhm.driveup.proto.GpgData}
 */
public  final class GpgData extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:net.nyhm.driveup.proto.GpgData)
    GpgDataOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GpgData.newBuilder() to construct.
  private GpgData(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GpgData() {
    recipient_ = "";
    publicKey_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GpgData(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            recipient_ = s;
            break;
          }
          case 18: {

            publicKey_ = input.readBytes();
            break;
          }
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_GpgData_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_GpgData_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            net.nyhm.driveup.proto.GpgData.class, net.nyhm.driveup.proto.GpgData.Builder.class);
  }

  public static final int RECIPIENT_FIELD_NUMBER = 1;
  private volatile java.lang.Object recipient_;
  /**
   * <code>string recipient = 1;</code>
   */
  public java.lang.String getRecipient() {
    java.lang.Object ref = recipient_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      recipient_ = s;
      return s;
    }
  }
  /**
   * <code>string recipient = 1;</code>
   */
  public com.google.protobuf.ByteString
      getRecipientBytes() {
    java.lang.Object ref = recipient_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      recipient_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int PUBLICKEY_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString publicKey_;
  /**
   * <code>bytes publicKey = 2;</code>
   */
  public com.google.protobuf.ByteString getPublicKey() {
    return publicKey_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getRecipientBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, recipient_);
    }
    if (!publicKey_.isEmpty()) {
      output.writeBytes(2, publicKey_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getRecipientBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, recipient_);
    }
    if (!publicKey_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, publicKey_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof net.nyhm.driveup.proto.GpgData)) {
      return super.equals(obj);
    }
    net.nyhm.driveup.proto.GpgData other = (net.nyhm.driveup.proto.GpgData) obj;

    boolean result = true;
    result = result && getRecipient()
        .equals(other.getRecipient());
    result = result && getPublicKey()
        .equals(other.getPublicKey());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + RECIPIENT_FIELD_NUMBER;
    hash = (53 * hash) + getRecipient().hashCode();
    hash = (37 * hash) + PUBLICKEY_FIELD_NUMBER;
    hash = (53 * hash) + getPublicKey().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static net.nyhm.driveup.proto.GpgData parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.GpgData parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static net.nyhm.driveup.proto.GpgData parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.nyhm.driveup.proto.GpgData parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(net.nyhm.driveup.proto.GpgData prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code net.nyhm.driveup.proto.GpgData}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:net.nyhm.driveup.proto.GpgData)
      net.nyhm.driveup.proto.GpgDataOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_GpgData_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_GpgData_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              net.nyhm.driveup.proto.GpgData.class, net.nyhm.driveup.proto.GpgData.Builder.class);
    }

    // Construct using net.nyhm.driveup.proto.GpgData.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      recipient_ = "";

      publicKey_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_GpgData_descriptor;
    }

    @java.lang.Override
    public net.nyhm.driveup.proto.GpgData getDefaultInstanceForType() {
      return net.nyhm.driveup.proto.GpgData.getDefaultInstance();
    }

    @java.lang.Override
    public net.nyhm.driveup.proto.GpgData build() {
      net.nyhm.driveup.proto.GpgData result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public net.nyhm.driveup.proto.GpgData buildPartial() {
      net.nyhm.driveup.proto.GpgData result = new net.nyhm.driveup.proto.GpgData(this);
      result.recipient_ = recipient_;
      result.publicKey_ = publicKey_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return (Builder) super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof net.nyhm.driveup.proto.GpgData) {
        return mergeFrom((net.nyhm.driveup.proto.GpgData)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(net.nyhm.driveup.proto.GpgData other) {
      if (other == net.nyhm.driveup.proto.GpgData.getDefaultInstance()) return this;
      if (!other.getRecipient().isEmpty()) {
        recipient_ = other.recipient_;
        onChanged();
      }
      if (other.getPublicKey() != com.google.protobuf.ByteString.EMPTY) {
        setPublicKey(other.getPublicKey());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      net.nyhm.driveup.proto.GpgData parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (net.nyhm.driveup.proto.GpgData) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object recipient_ = "";
    /**
     * <code>string recipient = 1;</code>
     */
    public java.lang.String getRecipient() {
      java.lang.Object ref = recipient_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        recipient_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string recipient = 1;</code>
     */
    public com.google.protobuf.ByteString
        getRecipientBytes() {
      java.lang.Object ref = recipient_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        recipient_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string recipient = 1;</code>
     */
    public Builder setRecipient(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      recipient_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string recipient = 1;</code>
     */
    public Builder clearRecipient() {
      
      recipient_ = getDefaultInstance().getRecipient();
      onChanged();
      return this;
    }
    /**
     * <code>string recipient = 1;</code>
     */
    public Builder setRecipientBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      recipient_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString publicKey_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes publicKey = 2;</code>
     */
    public com.google.protobuf.ByteString getPublicKey() {
      return publicKey_;
    }
    /**
     * <code>bytes publicKey = 2;</code>
     */
    public Builder setPublicKey(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      publicKey_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes publicKey = 2;</code>
     */
    public Builder clearPublicKey() {
      
      publicKey_ = getDefaultInstance().getPublicKey();
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:net.nyhm.driveup.proto.GpgData)
  }

  // @@protoc_insertion_point(class_scope:net.nyhm.driveup.proto.GpgData)
  private static final net.nyhm.driveup.proto.GpgData DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new net.nyhm.driveup.proto.GpgData();
  }

  public static net.nyhm.driveup.proto.GpgData getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GpgData>
      PARSER = new com.google.protobuf.AbstractParser<GpgData>() {
    @java.lang.Override
    public GpgData parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GpgData(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GpgData> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GpgData> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public net.nyhm.driveup.proto.GpgData getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
