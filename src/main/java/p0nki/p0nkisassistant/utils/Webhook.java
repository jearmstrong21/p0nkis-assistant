package p0nki.p0nkisassistant.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import p0nki.p0nkisassistant.P0nkisAssistant;
import p0nki.p0nkisassistant.data.BotConfig;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Webhook {

    //    private final List<WebhookClient> clients;
    private final WebhookClient main;
    private final Map<String, List<WebhookClient>> contexts;

    private static final Map<String, Webhook> webhooks = new HashMap<>();

    private Webhook(WebhookInstanceJSON instanceJSON) {
        main = from(instanceJSON.main);
        contexts = instanceJSON.contexts.entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().stream().map(Webhook::from).collect(Collectors.toList()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void initializeClients() {
        webhooks.clear();
        WebhookJSON json = Utils.deserialize("webhooks", WebhookJSON.class);
        json.webhooks.forEach((key, instanceJSON) -> webhooks.put(key, new Webhook(instanceJSON)));
    }

    private static WebhookClient from(String name) {
        try {
            return WebhookClient.withUrl(Utils.load("webhook:" + name + ".txt"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static Webhook get(String name) {
        return Objects.requireNonNull(webhooks.get(name));
    }

    public void accept(String ctx, WebhookMessageBuilder webhookMessageBuilder) {
        WebhookMessage message = webhookMessageBuilder
                .setUsername("p0nki's assistant: " + Utils.instanceName())
                .setAvatarUrl(P0nkisAssistant.jda.getSelfUser().getEffectiveAvatarUrl())
                .build();
        main.send(message).join();
        if (ctx != null && contexts.containsKey(ctx)) {
            contexts.get(ctx).forEach(client -> client.send(message).join());
        }
//        clients.forEach(client -> client.send(message).join());
    }

    public void accept(String ctx, WebhookEmbedBuilder webhookEmbedBuilder) {
        accept(ctx, new WebhookMessageBuilder().addEmbeds(webhookEmbedBuilder.build()));
    }

    public void ping(String ctx) {
        accept(ctx, new WebhookMessageBuilder().setContent(BotConfig.get().notifRolePing));
    }

    private static class WebhookInstanceJSON {
        public String main = "";
        public Map<String, List<String>> contexts = new HashMap<>();
    }

    private static class WebhookJSON {
        public Map<String, WebhookInstanceJSON> webhooks = new HashMap<>();
    }

}
