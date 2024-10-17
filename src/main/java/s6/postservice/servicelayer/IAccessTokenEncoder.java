package s6.postservice.servicelayer;


import s6.postservice.configuration.AccessToken;

public interface IAccessTokenEncoder {
    String encode(AccessToken accessToken);
}
