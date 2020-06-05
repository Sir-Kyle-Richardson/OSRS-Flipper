package com.flipper;

import com.flipper.controller.GrandExchangeController;
import com.flipper.controller.TradePersisterController;
import com.flipper.controller.FlipperController;
import com.flipper.model.Transaction;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "Flipper")
public class FlipperPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private FlipperConfig config;

	private FlipperController flipperController;

	@Override
	protected void startUp() throws Exception {
		log.info("Flipper started!");
		flipperController = new FlipperController();
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Flipper stopped!");
		flipperController.saveAll();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Flipper says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged newOfferEvent) {
		Transaction transaction = GrandExchangeController.handleOnGrandExchangeOfferChanged(newOfferEvent);
		// Transaction created. Save to json
		if (transaction != null) {
			// boolean isSaved = tradePersisterController.saveTransactions();
			flipperController.addTransaction(transaction);
		}
	}

	@Provides
	FlipperConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(FlipperConfig.class);
	}
}
