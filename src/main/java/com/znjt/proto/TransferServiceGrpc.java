package com.znjt.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.19.0)",
    comments = "Source: Transfer.proto")
public final class TransferServiceGrpc {

  private TransferServiceGrpc() {}

  public static final String SERVICE_NAME = "com.znjt.proto.TransferService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest,
      com.znjt.proto.SyncDataResponse> getTransporterByStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transporterByStream",
      requestType = com.znjt.proto.SyncDataRequest.class,
      responseType = com.znjt.proto.SyncDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest,
      com.znjt.proto.SyncDataResponse> getTransporterByStreamMethod() {
    io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest, com.znjt.proto.SyncDataResponse> getTransporterByStreamMethod;
    if ((getTransporterByStreamMethod = TransferServiceGrpc.getTransporterByStreamMethod) == null) {
      synchronized (TransferServiceGrpc.class) {
        if ((getTransporterByStreamMethod = TransferServiceGrpc.getTransporterByStreamMethod) == null) {
          TransferServiceGrpc.getTransporterByStreamMethod = getTransporterByStreamMethod = 
              io.grpc.MethodDescriptor.<com.znjt.proto.SyncDataRequest, com.znjt.proto.SyncDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "com.znjt.proto.TransferService", "transporterByStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TransferServiceMethodDescriptorSupplier("transporterByStream"))
                  .build();
          }
        }
     }
     return getTransporterByStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest,
      com.znjt.proto.SyncDataResponse> getTransporterBySyncMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transporterBySync",
      requestType = com.znjt.proto.SyncDataRequest.class,
      responseType = com.znjt.proto.SyncDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest,
      com.znjt.proto.SyncDataResponse> getTransporterBySyncMethod() {
    io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest, com.znjt.proto.SyncDataResponse> getTransporterBySyncMethod;
    if ((getTransporterBySyncMethod = TransferServiceGrpc.getTransporterBySyncMethod) == null) {
      synchronized (TransferServiceGrpc.class) {
        if ((getTransporterBySyncMethod = TransferServiceGrpc.getTransporterBySyncMethod) == null) {
          TransferServiceGrpc.getTransporterBySyncMethod = getTransporterBySyncMethod = 
              io.grpc.MethodDescriptor.<com.znjt.proto.SyncDataRequest, com.znjt.proto.SyncDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.znjt.proto.TransferService", "transporterBySync"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TransferServiceMethodDescriptorSupplier("transporterBySync"))
                  .build();
          }
        }
     }
     return getTransporterBySyncMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest,
      com.znjt.proto.SyncDataResponse> getTransporterBySyncTestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transporterBySyncTest",
      requestType = com.znjt.proto.SyncDataRequest.class,
      responseType = com.znjt.proto.SyncDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest,
      com.znjt.proto.SyncDataResponse> getTransporterBySyncTestMethod() {
    io.grpc.MethodDescriptor<com.znjt.proto.SyncDataRequest, com.znjt.proto.SyncDataResponse> getTransporterBySyncTestMethod;
    if ((getTransporterBySyncTestMethod = TransferServiceGrpc.getTransporterBySyncTestMethod) == null) {
      synchronized (TransferServiceGrpc.class) {
        if ((getTransporterBySyncTestMethod = TransferServiceGrpc.getTransporterBySyncTestMethod) == null) {
          TransferServiceGrpc.getTransporterBySyncTestMethod = getTransporterBySyncTestMethod = 
              io.grpc.MethodDescriptor.<com.znjt.proto.SyncDataRequest, com.znjt.proto.SyncDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.znjt.proto.TransferService", "transporterBySyncTest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TransferServiceMethodDescriptorSupplier("transporterBySyncTest"))
                  .build();
          }
        }
     }
     return getTransporterBySyncTestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.znjt.proto.SyncMulImgRequest,
      com.znjt.proto.SyncMulImgResponse> getTransporterMulBySyncMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transporterMulBySync",
      requestType = com.znjt.proto.SyncMulImgRequest.class,
      responseType = com.znjt.proto.SyncMulImgResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.znjt.proto.SyncMulImgRequest,
      com.znjt.proto.SyncMulImgResponse> getTransporterMulBySyncMethod() {
    io.grpc.MethodDescriptor<com.znjt.proto.SyncMulImgRequest, com.znjt.proto.SyncMulImgResponse> getTransporterMulBySyncMethod;
    if ((getTransporterMulBySyncMethod = TransferServiceGrpc.getTransporterMulBySyncMethod) == null) {
      synchronized (TransferServiceGrpc.class) {
        if ((getTransporterMulBySyncMethod = TransferServiceGrpc.getTransporterMulBySyncMethod) == null) {
          TransferServiceGrpc.getTransporterMulBySyncMethod = getTransporterMulBySyncMethod = 
              io.grpc.MethodDescriptor.<com.znjt.proto.SyncMulImgRequest, com.znjt.proto.SyncMulImgResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.znjt.proto.TransferService", "transporterMulBySync"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncMulImgRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncMulImgResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TransferServiceMethodDescriptorSupplier("transporterMulBySync"))
                  .build();
          }
        }
     }
     return getTransporterMulBySyncMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.znjt.proto.SyncMulSingleImgRequest,
      com.znjt.proto.SyncMulSingleImgResponse> getTransporterMulSingleBySyncMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transporterMulSingleBySync",
      requestType = com.znjt.proto.SyncMulSingleImgRequest.class,
      responseType = com.znjt.proto.SyncMulSingleImgResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.znjt.proto.SyncMulSingleImgRequest,
      com.znjt.proto.SyncMulSingleImgResponse> getTransporterMulSingleBySyncMethod() {
    io.grpc.MethodDescriptor<com.znjt.proto.SyncMulSingleImgRequest, com.znjt.proto.SyncMulSingleImgResponse> getTransporterMulSingleBySyncMethod;
    if ((getTransporterMulSingleBySyncMethod = TransferServiceGrpc.getTransporterMulSingleBySyncMethod) == null) {
      synchronized (TransferServiceGrpc.class) {
        if ((getTransporterMulSingleBySyncMethod = TransferServiceGrpc.getTransporterMulSingleBySyncMethod) == null) {
          TransferServiceGrpc.getTransporterMulSingleBySyncMethod = getTransporterMulSingleBySyncMethod = 
              io.grpc.MethodDescriptor.<com.znjt.proto.SyncMulSingleImgRequest, com.znjt.proto.SyncMulSingleImgResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.znjt.proto.TransferService", "transporterMulSingleBySync"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncMulSingleImgRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.znjt.proto.SyncMulSingleImgResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TransferServiceMethodDescriptorSupplier("transporterMulSingleBySync"))
                  .build();
          }
        }
     }
     return getTransporterMulSingleBySyncMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TransferServiceStub newStub(io.grpc.Channel channel) {
    return new TransferServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TransferServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TransferServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TransferServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TransferServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class TransferServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataRequest> transporterByStream(
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getTransporterByStreamMethod(), responseObserver);
    }

    /**
     */
    public void transporterBySync(com.znjt.proto.SyncDataRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransporterBySyncMethod(), responseObserver);
    }

    /**
     */
    public void transporterBySyncTest(com.znjt.proto.SyncDataRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransporterBySyncTestMethod(), responseObserver);
    }

    /**
     */
    public void transporterMulBySync(com.znjt.proto.SyncMulImgRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncMulImgResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransporterMulBySyncMethod(), responseObserver);
    }

    /**
     */
    public void transporterMulSingleBySync(com.znjt.proto.SyncMulSingleImgRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncMulSingleImgResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransporterMulSingleBySyncMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getTransporterByStreamMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.znjt.proto.SyncDataRequest,
                com.znjt.proto.SyncDataResponse>(
                  this, METHODID_TRANSPORTER_BY_STREAM)))
          .addMethod(
            getTransporterBySyncMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.znjt.proto.SyncDataRequest,
                com.znjt.proto.SyncDataResponse>(
                  this, METHODID_TRANSPORTER_BY_SYNC)))
          .addMethod(
            getTransporterBySyncTestMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.znjt.proto.SyncDataRequest,
                com.znjt.proto.SyncDataResponse>(
                  this, METHODID_TRANSPORTER_BY_SYNC_TEST)))
          .addMethod(
            getTransporterMulBySyncMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.znjt.proto.SyncMulImgRequest,
                com.znjt.proto.SyncMulImgResponse>(
                  this, METHODID_TRANSPORTER_MUL_BY_SYNC)))
          .addMethod(
            getTransporterMulSingleBySyncMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.znjt.proto.SyncMulSingleImgRequest,
                com.znjt.proto.SyncMulSingleImgResponse>(
                  this, METHODID_TRANSPORTER_MUL_SINGLE_BY_SYNC)))
          .build();
    }
  }

  /**
   */
  public static final class TransferServiceStub extends io.grpc.stub.AbstractStub<TransferServiceStub> {
    private TransferServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TransferServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransferServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TransferServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataRequest> transporterByStream(
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getTransporterByStreamMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void transporterBySync(com.znjt.proto.SyncDataRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransporterBySyncMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transporterBySyncTest(com.znjt.proto.SyncDataRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransporterBySyncTestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transporterMulBySync(com.znjt.proto.SyncMulImgRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncMulImgResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransporterMulBySyncMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transporterMulSingleBySync(com.znjt.proto.SyncMulSingleImgRequest request,
        io.grpc.stub.StreamObserver<com.znjt.proto.SyncMulSingleImgResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransporterMulSingleBySyncMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TransferServiceBlockingStub extends io.grpc.stub.AbstractStub<TransferServiceBlockingStub> {
    private TransferServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TransferServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransferServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TransferServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.znjt.proto.SyncDataResponse transporterBySync(com.znjt.proto.SyncDataRequest request) {
      return blockingUnaryCall(
          getChannel(), getTransporterBySyncMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.znjt.proto.SyncDataResponse transporterBySyncTest(com.znjt.proto.SyncDataRequest request) {
      return blockingUnaryCall(
          getChannel(), getTransporterBySyncTestMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.znjt.proto.SyncMulImgResponse transporterMulBySync(com.znjt.proto.SyncMulImgRequest request) {
      return blockingUnaryCall(
          getChannel(), getTransporterMulBySyncMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.znjt.proto.SyncMulSingleImgResponse transporterMulSingleBySync(com.znjt.proto.SyncMulSingleImgRequest request) {
      return blockingUnaryCall(
          getChannel(), getTransporterMulSingleBySyncMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TransferServiceFutureStub extends io.grpc.stub.AbstractStub<TransferServiceFutureStub> {
    private TransferServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TransferServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TransferServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TransferServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.znjt.proto.SyncDataResponse> transporterBySync(
        com.znjt.proto.SyncDataRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTransporterBySyncMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.znjt.proto.SyncDataResponse> transporterBySyncTest(
        com.znjt.proto.SyncDataRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTransporterBySyncTestMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.znjt.proto.SyncMulImgResponse> transporterMulBySync(
        com.znjt.proto.SyncMulImgRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTransporterMulBySyncMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.znjt.proto.SyncMulSingleImgResponse> transporterMulSingleBySync(
        com.znjt.proto.SyncMulSingleImgRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTransporterMulSingleBySyncMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TRANSPORTER_BY_SYNC = 0;
  private static final int METHODID_TRANSPORTER_BY_SYNC_TEST = 1;
  private static final int METHODID_TRANSPORTER_MUL_BY_SYNC = 2;
  private static final int METHODID_TRANSPORTER_MUL_SINGLE_BY_SYNC = 3;
  private static final int METHODID_TRANSPORTER_BY_STREAM = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TransferServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TransferServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRANSPORTER_BY_SYNC:
          serviceImpl.transporterBySync((com.znjt.proto.SyncDataRequest) request,
              (io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse>) responseObserver);
          break;
        case METHODID_TRANSPORTER_BY_SYNC_TEST:
          serviceImpl.transporterBySyncTest((com.znjt.proto.SyncDataRequest) request,
              (io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse>) responseObserver);
          break;
        case METHODID_TRANSPORTER_MUL_BY_SYNC:
          serviceImpl.transporterMulBySync((com.znjt.proto.SyncMulImgRequest) request,
              (io.grpc.stub.StreamObserver<com.znjt.proto.SyncMulImgResponse>) responseObserver);
          break;
        case METHODID_TRANSPORTER_MUL_SINGLE_BY_SYNC:
          serviceImpl.transporterMulSingleBySync((com.znjt.proto.SyncMulSingleImgRequest) request,
              (io.grpc.stub.StreamObserver<com.znjt.proto.SyncMulSingleImgResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRANSPORTER_BY_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.transporterByStream(
              (io.grpc.stub.StreamObserver<com.znjt.proto.SyncDataResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TransferServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TransferServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.znjt.proto.TransferProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TransferService");
    }
  }

  private static final class TransferServiceFileDescriptorSupplier
      extends TransferServiceBaseDescriptorSupplier {
    TransferServiceFileDescriptorSupplier() {}
  }

  private static final class TransferServiceMethodDescriptorSupplier
      extends TransferServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TransferServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TransferServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TransferServiceFileDescriptorSupplier())
              .addMethod(getTransporterByStreamMethod())
              .addMethod(getTransporterBySyncMethod())
              .addMethod(getTransporterBySyncTestMethod())
              .addMethod(getTransporterMulBySyncMethod())
              .addMethod(getTransporterMulSingleBySyncMethod())
              .build();
        }
      }
    }
    return result;
  }
}
