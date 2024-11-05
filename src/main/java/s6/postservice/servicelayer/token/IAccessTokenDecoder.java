package s6.postservice.servicelayer.token;


import s6.postservice.configuration.AccessToken;

public interface IAccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);
}
