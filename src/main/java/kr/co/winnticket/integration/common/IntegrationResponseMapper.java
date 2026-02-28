package kr.co.winnticket.integration.common;

public interface IntegrationResponseMapper<T> {
    IntegrationResult map(T response);
}