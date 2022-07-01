# blastream-java-sdk

All URIs are relative to *https://api.v2.blastream.com/api-docs*  
This is also where you can find all parameters related to params variable

### Examples

### createOrGetChannel

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY","PRIVATE_KEY", "");
  JSONObject params = new JSONObject();
  Channel channel = instance.createOrGetChannel(name,params);
  instance.getIframe();
}
```
### createCollaborator

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY","PRIVATE_KEY", "");
  JSONObject paramsChannel = new JSONObject();
  JSONObject paramsCollab = new JSONObject();
  paramsCollab.put("email","user@example.com");
  Channel channel = instance.createOrGetChannel(name,paramsChannel);
  Collaborator colabname = channel.createOrGetCollaborator(name, status, paramsCollab);
  channel.getIframe();
 }
  ```
  ### create participant
  
  ```java
  public static void main(String[] args){
    Instance instance = new Instance("PUBLIC_KEY","PRIVATE_KEY", "");
    JSONObject paramsChannel = new JSONObject();
    JSONObject paramsParticipant = new JSONObject();
    Channel channel = instance.createOrGetChannel("channel name",paramsChannel);
    instance.createOrGetParticipant(channelslug,participant id, paramsParticipant); 
    instance.getIframe();
}
```

### getReplays

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY", "PRIVATE_KEY", "");
  JSONObject params = new JSONObject();
  Channel channel = instance.createOrGetChannel(name, params);
  channel.getReplays();
}
```

### setAccessRule

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY", "PRIVATE_KEY", "");
  JSONObject params = new JSONObject();
  Channel channel = instance.createOrGetChannel(name, params);
  JSONObject AccessRule = new JSONObject();
  AccessRule.put("password", "password");
  channel.setAccessRule("PRIVATE", AccessRule);
}
```

### updateSubscription

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY", "PRIVATE_KEY", "");
  JSONObject params = new JSONObject();
  Channel channel = instance.createOrGetChannel(name, params);
  channel.updateSubscription("pro2","hourly");
}
```

### updateSettings

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY", "PRIVATE_KEY", "");
  JSONObject params = new JSONObject();
  Channel channel = instance.createOrGetChannel(name, params);
  params.put("autojoin", 1);
  channel.updateSettings(params);
}
```

### updateCollaborator

```java
public static void main(String[] args){
  Instance instance = new Instance("PUBLIC_KEY", "PRIVATE_KEY", "");
  JSONObject params = new JSONObject();
  Channel channel = instance.createOrGetChannel(name, params);
  Collaborator colab = channel.createOrGetCollaborator("username","moderator");
  colab.update("new username","animator");
}
```
