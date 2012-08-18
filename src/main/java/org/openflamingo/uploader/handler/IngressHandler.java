package org.openflamingo.uploader.handler;

/**
 * Ingress 처리를 하는 핸들러 인터페이스.
 *
 * @author Edward KIM
 * @since 0.1
 */
public interface IngressHandler {

    void execute();

    IngressHandler validate();

}
