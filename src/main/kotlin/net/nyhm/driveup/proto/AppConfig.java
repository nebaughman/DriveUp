// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: creds.proto

package net.nyhm.driveup.proto;

/**
 * Protobuf type {@code net.nyhm.driveup.proto.AppConfig}
 */
public  final class AppConfig extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:net.nyhm.driveup.proto.AppConfig)
    AppConfigOrBuilder {
private static final long serialVersionUID = 0L;
  // Use AppConfig.newBuilder() to construct.
  private AppConfig(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private AppConfig() {
    version_ = 0;
    appName_ = "";
    clientSecrets_ = com.google.protobuf.ByteString.EMPTY;
    access_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private AppConfig(
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
          case 8: {

            version_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            appName_ = s;
            break;
          }
          case 26: {

            clientSecrets_ = input.readBytes();
            break;
          }
          case 32: {
            int rawValue = input.readEnum();

            access_ = rawValue;
            break;
          }
          case 42: {
            if (!((mutable_bitField0_ & 0x00000010) == 0x00000010)) {
              credsStore_ = com.google.protobuf.MapField.newMapField(
                  CredsStoreDefaultEntryHolder.defaultEntry);
              mutable_bitField0_ |= 0x00000010;
            }
            com.google.protobuf.MapEntry<java.lang.String, net.nyhm.driveup.proto.CredsData>
            credsStore__ = input.readMessage(
                CredsStoreDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
            credsStore_.getMutableMap().put(
                credsStore__.getKey(), credsStore__.getValue());
            break;
          }
          case 50: {
            net.nyhm.driveup.proto.GpgData.Builder subBuilder = null;
            if (gpgData_ != null) {
              subBuilder = gpgData_.toBuilder();
            }
            gpgData_ = input.readMessage(net.nyhm.driveup.proto.GpgData.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(gpgData_);
              gpgData_ = subBuilder.buildPartial();
            }

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
    return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_AppConfig_descriptor;
  }

  @SuppressWarnings({"rawtypes"})
  @java.lang.Override
  protected com.google.protobuf.MapField internalGetMapField(
      int number) {
    switch (number) {
      case 5:
        return internalGetCredsStore();
      default:
        throw new RuntimeException(
            "Invalid map field number: " + number);
    }
  }
  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_AppConfig_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            net.nyhm.driveup.proto.AppConfig.class, net.nyhm.driveup.proto.AppConfig.Builder.class);
  }

  private int bitField0_;
  public static final int VERSION_FIELD_NUMBER = 1;
  private int version_;
  /**
   * <code>int32 version = 1;</code>
   */
  public int getVersion() {
    return version_;
  }

  public static final int APP_NAME_FIELD_NUMBER = 2;
  private volatile java.lang.Object appName_;
  /**
   * <code>string app_name = 2;</code>
   */
  public java.lang.String getAppName() {
    java.lang.Object ref = appName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      appName_ = s;
      return s;
    }
  }
  /**
   * <code>string app_name = 2;</code>
   */
  public com.google.protobuf.ByteString
      getAppNameBytes() {
    java.lang.Object ref = appName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      appName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CLIENT_SECRETS_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString clientSecrets_;
  /**
   * <code>bytes client_secrets = 3;</code>
   */
  public com.google.protobuf.ByteString getClientSecrets() {
    return clientSecrets_;
  }

  public static final int ACCESS_FIELD_NUMBER = 4;
  private int access_;
  /**
   * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
   */
  public int getAccessValue() {
    return access_;
  }
  /**
   * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
   */
  public net.nyhm.driveup.proto.Access getAccess() {
    @SuppressWarnings("deprecation")
    net.nyhm.driveup.proto.Access result = net.nyhm.driveup.proto.Access.valueOf(access_);
    return result == null ? net.nyhm.driveup.proto.Access.UNRECOGNIZED : result;
  }

  public static final int CREDS_STORE_FIELD_NUMBER = 5;
  private static final class CredsStoreDefaultEntryHolder {
    static final com.google.protobuf.MapEntry<
        java.lang.String, net.nyhm.driveup.proto.CredsData> defaultEntry =
            com.google.protobuf.MapEntry
            .<java.lang.String, net.nyhm.driveup.proto.CredsData>newDefaultInstance(
                net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_AppConfig_CredsStoreEntry_descriptor, 
                com.google.protobuf.WireFormat.FieldType.STRING,
                "",
                com.google.protobuf.WireFormat.FieldType.MESSAGE,
                net.nyhm.driveup.proto.CredsData.getDefaultInstance());
  }
  private com.google.protobuf.MapField<
      java.lang.String, net.nyhm.driveup.proto.CredsData> credsStore_;
  private com.google.protobuf.MapField<java.lang.String, net.nyhm.driveup.proto.CredsData>
  internalGetCredsStore() {
    if (credsStore_ == null) {
      return com.google.protobuf.MapField.emptyMapField(
          CredsStoreDefaultEntryHolder.defaultEntry);
    }
    return credsStore_;
  }

  public int getCredsStoreCount() {
    return internalGetCredsStore().getMap().size();
  }
  /**
   * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
   */

  public boolean containsCredsStore(
      java.lang.String key) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    return internalGetCredsStore().getMap().containsKey(key);
  }
  /**
   * Use {@link #getCredsStoreMap()} instead.
   */
  @java.lang.Deprecated
  public java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> getCredsStore() {
    return getCredsStoreMap();
  }
  /**
   * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
   */

  public java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> getCredsStoreMap() {
    return internalGetCredsStore().getMap();
  }
  /**
   * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
   */

  public net.nyhm.driveup.proto.CredsData getCredsStoreOrDefault(
      java.lang.String key,
      net.nyhm.driveup.proto.CredsData defaultValue) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> map =
        internalGetCredsStore().getMap();
    return map.containsKey(key) ? map.get(key) : defaultValue;
  }
  /**
   * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
   */

  public net.nyhm.driveup.proto.CredsData getCredsStoreOrThrow(
      java.lang.String key) {
    if (key == null) { throw new java.lang.NullPointerException(); }
    java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> map =
        internalGetCredsStore().getMap();
    if (!map.containsKey(key)) {
      throw new java.lang.IllegalArgumentException();
    }
    return map.get(key);
  }

  public static final int GPG_DATA_FIELD_NUMBER = 6;
  private net.nyhm.driveup.proto.GpgData gpgData_;
  /**
   * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
   */
  public boolean hasGpgData() {
    return gpgData_ != null;
  }
  /**
   * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
   */
  public net.nyhm.driveup.proto.GpgData getGpgData() {
    return gpgData_ == null ? net.nyhm.driveup.proto.GpgData.getDefaultInstance() : gpgData_;
  }
  /**
   * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
   */
  public net.nyhm.driveup.proto.GpgDataOrBuilder getGpgDataOrBuilder() {
    return getGpgData();
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
    if (version_ != 0) {
      output.writeInt32(1, version_);
    }
    if (!getAppNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, appName_);
    }
    if (!clientSecrets_.isEmpty()) {
      output.writeBytes(3, clientSecrets_);
    }
    if (access_ != net.nyhm.driveup.proto.Access.READ.getNumber()) {
      output.writeEnum(4, access_);
    }
    com.google.protobuf.GeneratedMessageV3
      .serializeStringMapTo(
        output,
        internalGetCredsStore(),
        CredsStoreDefaultEntryHolder.defaultEntry,
        5);
    if (gpgData_ != null) {
      output.writeMessage(6, getGpgData());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (version_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, version_);
    }
    if (!getAppNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, appName_);
    }
    if (!clientSecrets_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, clientSecrets_);
    }
    if (access_ != net.nyhm.driveup.proto.Access.READ.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(4, access_);
    }
    for (java.util.Map.Entry<java.lang.String, net.nyhm.driveup.proto.CredsData> entry
         : internalGetCredsStore().getMap().entrySet()) {
      com.google.protobuf.MapEntry<java.lang.String, net.nyhm.driveup.proto.CredsData>
      credsStore__ = CredsStoreDefaultEntryHolder.defaultEntry.newBuilderForType()
          .setKey(entry.getKey())
          .setValue(entry.getValue())
          .build();
      size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, credsStore__);
    }
    if (gpgData_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(6, getGpgData());
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
    if (!(obj instanceof net.nyhm.driveup.proto.AppConfig)) {
      return super.equals(obj);
    }
    net.nyhm.driveup.proto.AppConfig other = (net.nyhm.driveup.proto.AppConfig) obj;

    boolean result = true;
    result = result && (getVersion()
        == other.getVersion());
    result = result && getAppName()
        .equals(other.getAppName());
    result = result && getClientSecrets()
        .equals(other.getClientSecrets());
    result = result && access_ == other.access_;
    result = result && internalGetCredsStore().equals(
        other.internalGetCredsStore());
    result = result && (hasGpgData() == other.hasGpgData());
    if (hasGpgData()) {
      result = result && getGpgData()
          .equals(other.getGpgData());
    }
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
    hash = (37 * hash) + VERSION_FIELD_NUMBER;
    hash = (53 * hash) + getVersion();
    hash = (37 * hash) + APP_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getAppName().hashCode();
    hash = (37 * hash) + CLIENT_SECRETS_FIELD_NUMBER;
    hash = (53 * hash) + getClientSecrets().hashCode();
    hash = (37 * hash) + ACCESS_FIELD_NUMBER;
    hash = (53 * hash) + access_;
    if (!internalGetCredsStore().getMap().isEmpty()) {
      hash = (37 * hash) + CREDS_STORE_FIELD_NUMBER;
      hash = (53 * hash) + internalGetCredsStore().hashCode();
    }
    if (hasGpgData()) {
      hash = (37 * hash) + GPG_DATA_FIELD_NUMBER;
      hash = (53 * hash) + getGpgData().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.AppConfig parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static net.nyhm.driveup.proto.AppConfig parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.nyhm.driveup.proto.AppConfig parseFrom(
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
  public static Builder newBuilder(net.nyhm.driveup.proto.AppConfig prototype) {
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
   * Protobuf type {@code net.nyhm.driveup.proto.AppConfig}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:net.nyhm.driveup.proto.AppConfig)
      net.nyhm.driveup.proto.AppConfigOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_AppConfig_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 5:
          return internalGetCredsStore();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMutableMapField(
        int number) {
      switch (number) {
        case 5:
          return internalGetMutableCredsStore();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_AppConfig_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              net.nyhm.driveup.proto.AppConfig.class, net.nyhm.driveup.proto.AppConfig.Builder.class);
    }

    // Construct using net.nyhm.driveup.proto.AppConfig.newBuilder()
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
      version_ = 0;

      appName_ = "";

      clientSecrets_ = com.google.protobuf.ByteString.EMPTY;

      access_ = 0;

      internalGetMutableCredsStore().clear();
      if (gpgDataBuilder_ == null) {
        gpgData_ = null;
      } else {
        gpgData_ = null;
        gpgDataBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return net.nyhm.driveup.proto.Creds.internal_static_net_nyhm_driveup_proto_AppConfig_descriptor;
    }

    @java.lang.Override
    public net.nyhm.driveup.proto.AppConfig getDefaultInstanceForType() {
      return net.nyhm.driveup.proto.AppConfig.getDefaultInstance();
    }

    @java.lang.Override
    public net.nyhm.driveup.proto.AppConfig build() {
      net.nyhm.driveup.proto.AppConfig result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public net.nyhm.driveup.proto.AppConfig buildPartial() {
      net.nyhm.driveup.proto.AppConfig result = new net.nyhm.driveup.proto.AppConfig(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.version_ = version_;
      result.appName_ = appName_;
      result.clientSecrets_ = clientSecrets_;
      result.access_ = access_;
      result.credsStore_ = internalGetCredsStore();
      result.credsStore_.makeImmutable();
      if (gpgDataBuilder_ == null) {
        result.gpgData_ = gpgData_;
      } else {
        result.gpgData_ = gpgDataBuilder_.build();
      }
      result.bitField0_ = to_bitField0_;
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
      if (other instanceof net.nyhm.driveup.proto.AppConfig) {
        return mergeFrom((net.nyhm.driveup.proto.AppConfig)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(net.nyhm.driveup.proto.AppConfig other) {
      if (other == net.nyhm.driveup.proto.AppConfig.getDefaultInstance()) return this;
      if (other.getVersion() != 0) {
        setVersion(other.getVersion());
      }
      if (!other.getAppName().isEmpty()) {
        appName_ = other.appName_;
        onChanged();
      }
      if (other.getClientSecrets() != com.google.protobuf.ByteString.EMPTY) {
        setClientSecrets(other.getClientSecrets());
      }
      if (other.access_ != 0) {
        setAccessValue(other.getAccessValue());
      }
      internalGetMutableCredsStore().mergeFrom(
          other.internalGetCredsStore());
      if (other.hasGpgData()) {
        mergeGpgData(other.getGpgData());
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
      net.nyhm.driveup.proto.AppConfig parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (net.nyhm.driveup.proto.AppConfig) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int version_ ;
    /**
     * <code>int32 version = 1;</code>
     */
    public int getVersion() {
      return version_;
    }
    /**
     * <code>int32 version = 1;</code>
     */
    public Builder setVersion(int value) {
      
      version_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 version = 1;</code>
     */
    public Builder clearVersion() {
      
      version_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object appName_ = "";
    /**
     * <code>string app_name = 2;</code>
     */
    public java.lang.String getAppName() {
      java.lang.Object ref = appName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        appName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string app_name = 2;</code>
     */
    public com.google.protobuf.ByteString
        getAppNameBytes() {
      java.lang.Object ref = appName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        appName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string app_name = 2;</code>
     */
    public Builder setAppName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      appName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string app_name = 2;</code>
     */
    public Builder clearAppName() {
      
      appName_ = getDefaultInstance().getAppName();
      onChanged();
      return this;
    }
    /**
     * <code>string app_name = 2;</code>
     */
    public Builder setAppNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      appName_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString clientSecrets_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes client_secrets = 3;</code>
     */
    public com.google.protobuf.ByteString getClientSecrets() {
      return clientSecrets_;
    }
    /**
     * <code>bytes client_secrets = 3;</code>
     */
    public Builder setClientSecrets(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      clientSecrets_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes client_secrets = 3;</code>
     */
    public Builder clearClientSecrets() {
      
      clientSecrets_ = getDefaultInstance().getClientSecrets();
      onChanged();
      return this;
    }

    private int access_ = 0;
    /**
     * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
     */
    public int getAccessValue() {
      return access_;
    }
    /**
     * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
     */
    public Builder setAccessValue(int value) {
      access_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
     */
    public net.nyhm.driveup.proto.Access getAccess() {
      @SuppressWarnings("deprecation")
      net.nyhm.driveup.proto.Access result = net.nyhm.driveup.proto.Access.valueOf(access_);
      return result == null ? net.nyhm.driveup.proto.Access.UNRECOGNIZED : result;
    }
    /**
     * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
     */
    public Builder setAccess(net.nyhm.driveup.proto.Access value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      access_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.net.nyhm.driveup.proto.Access access = 4;</code>
     */
    public Builder clearAccess() {
      
      access_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.MapField<
        java.lang.String, net.nyhm.driveup.proto.CredsData> credsStore_;
    private com.google.protobuf.MapField<java.lang.String, net.nyhm.driveup.proto.CredsData>
    internalGetCredsStore() {
      if (credsStore_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            CredsStoreDefaultEntryHolder.defaultEntry);
      }
      return credsStore_;
    }
    private com.google.protobuf.MapField<java.lang.String, net.nyhm.driveup.proto.CredsData>
    internalGetMutableCredsStore() {
      onChanged();;
      if (credsStore_ == null) {
        credsStore_ = com.google.protobuf.MapField.newMapField(
            CredsStoreDefaultEntryHolder.defaultEntry);
      }
      if (!credsStore_.isMutable()) {
        credsStore_ = credsStore_.copy();
      }
      return credsStore_;
    }

    public int getCredsStoreCount() {
      return internalGetCredsStore().getMap().size();
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */

    public boolean containsCredsStore(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetCredsStore().getMap().containsKey(key);
    }
    /**
     * Use {@link #getCredsStoreMap()} instead.
     */
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> getCredsStore() {
      return getCredsStoreMap();
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */

    public java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> getCredsStoreMap() {
      return internalGetCredsStore().getMap();
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */

    public net.nyhm.driveup.proto.CredsData getCredsStoreOrDefault(
        java.lang.String key,
        net.nyhm.driveup.proto.CredsData defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> map =
          internalGetCredsStore().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */

    public net.nyhm.driveup.proto.CredsData getCredsStoreOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> map =
          internalGetCredsStore().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    public Builder clearCredsStore() {
      internalGetMutableCredsStore().getMutableMap()
          .clear();
      return this;
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */

    public Builder removeCredsStore(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      internalGetMutableCredsStore().getMutableMap()
          .remove(key);
      return this;
    }
    /**
     * Use alternate mutation accessors instead.
     */
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData>
    getMutableCredsStore() {
      return internalGetMutableCredsStore().getMutableMap();
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */
    public Builder putCredsStore(
        java.lang.String key,
        net.nyhm.driveup.proto.CredsData value) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      if (value == null) { throw new java.lang.NullPointerException(); }
      internalGetMutableCredsStore().getMutableMap()
          .put(key, value);
      return this;
    }
    /**
     * <code>map&lt;string, .net.nyhm.driveup.proto.CredsData&gt; creds_store = 5;</code>
     */

    public Builder putAllCredsStore(
        java.util.Map<java.lang.String, net.nyhm.driveup.proto.CredsData> values) {
      internalGetMutableCredsStore().getMutableMap()
          .putAll(values);
      return this;
    }

    private net.nyhm.driveup.proto.GpgData gpgData_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        net.nyhm.driveup.proto.GpgData, net.nyhm.driveup.proto.GpgData.Builder, net.nyhm.driveup.proto.GpgDataOrBuilder> gpgDataBuilder_;
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public boolean hasGpgData() {
      return gpgDataBuilder_ != null || gpgData_ != null;
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public net.nyhm.driveup.proto.GpgData getGpgData() {
      if (gpgDataBuilder_ == null) {
        return gpgData_ == null ? net.nyhm.driveup.proto.GpgData.getDefaultInstance() : gpgData_;
      } else {
        return gpgDataBuilder_.getMessage();
      }
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public Builder setGpgData(net.nyhm.driveup.proto.GpgData value) {
      if (gpgDataBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        gpgData_ = value;
        onChanged();
      } else {
        gpgDataBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public Builder setGpgData(
        net.nyhm.driveup.proto.GpgData.Builder builderForValue) {
      if (gpgDataBuilder_ == null) {
        gpgData_ = builderForValue.build();
        onChanged();
      } else {
        gpgDataBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public Builder mergeGpgData(net.nyhm.driveup.proto.GpgData value) {
      if (gpgDataBuilder_ == null) {
        if (gpgData_ != null) {
          gpgData_ =
            net.nyhm.driveup.proto.GpgData.newBuilder(gpgData_).mergeFrom(value).buildPartial();
        } else {
          gpgData_ = value;
        }
        onChanged();
      } else {
        gpgDataBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public Builder clearGpgData() {
      if (gpgDataBuilder_ == null) {
        gpgData_ = null;
        onChanged();
      } else {
        gpgData_ = null;
        gpgDataBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public net.nyhm.driveup.proto.GpgData.Builder getGpgDataBuilder() {
      
      onChanged();
      return getGpgDataFieldBuilder().getBuilder();
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    public net.nyhm.driveup.proto.GpgDataOrBuilder getGpgDataOrBuilder() {
      if (gpgDataBuilder_ != null) {
        return gpgDataBuilder_.getMessageOrBuilder();
      } else {
        return gpgData_ == null ?
            net.nyhm.driveup.proto.GpgData.getDefaultInstance() : gpgData_;
      }
    }
    /**
     * <code>.net.nyhm.driveup.proto.GpgData gpg_data = 6;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        net.nyhm.driveup.proto.GpgData, net.nyhm.driveup.proto.GpgData.Builder, net.nyhm.driveup.proto.GpgDataOrBuilder> 
        getGpgDataFieldBuilder() {
      if (gpgDataBuilder_ == null) {
        gpgDataBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            net.nyhm.driveup.proto.GpgData, net.nyhm.driveup.proto.GpgData.Builder, net.nyhm.driveup.proto.GpgDataOrBuilder>(
                getGpgData(),
                getParentForChildren(),
                isClean());
        gpgData_ = null;
      }
      return gpgDataBuilder_;
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


    // @@protoc_insertion_point(builder_scope:net.nyhm.driveup.proto.AppConfig)
  }

  // @@protoc_insertion_point(class_scope:net.nyhm.driveup.proto.AppConfig)
  private static final net.nyhm.driveup.proto.AppConfig DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new net.nyhm.driveup.proto.AppConfig();
  }

  public static net.nyhm.driveup.proto.AppConfig getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AppConfig>
      PARSER = new com.google.protobuf.AbstractParser<AppConfig>() {
    @java.lang.Override
    public AppConfig parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new AppConfig(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<AppConfig> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<AppConfig> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public net.nyhm.driveup.proto.AppConfig getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

