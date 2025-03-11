import { GoogleGenerativeAI } from '@google/generative-ai';
import { GeminiAIResponseHandler } from './GeminiAIResponseHandler';
import type { AIAgent } from '../types';
import type { Channel, DefaultGenerics, Event, StreamChat } from 'stream-chat';

export class GeminiAIAgent implements AIAgent {
  private genAI?: GoogleGenerativeAI;
  private model?: any; // Ganti 'any' dengan tipe yang sesuai
  private lastInteractionTs = Date.now();

  private handlers: GeminiAIResponseHandler[] = [];

  constructor(
    readonly chatClient: StreamChat,
    readonly channel: Channel,
  ) {}

  dispose = async () => {
    this.chatClient.off('message.new', this.handleMessage);
    await this.chatClient.disconnectUser();

    this.handlers.forEach((handler) => handler.dispose());
    this.handlers = [];
  };

  getLastInteraction = (): number => this.lastInteractionTs;

  init = async () => {
    const apiKey = process.env.GOOGLE_API_KEY as string | undefined;
    if (!apiKey) {
      throw new Error('Google API key is required');
    }

    this.genAI = new GoogleGenerativeAI(apiKey);
    this.model = this.genAI.getGenerativeModel({ model: 'gemini-2.0-flash' }); // Sesuaikan dengan model yang ingin digunakan

    this.chatClient.on('message.new', this.handleMessage);
  };

  private handleMessage = async (e: Event<DefaultGenerics>) => {
    if (!this.genAI || !this.model) {
      console.log('Gemini not initialized');
      return;
    }

    if (!e.message || e.message.ai_generated) {
      console.log('Skip handling ai generated message');
      return;
    }

    const message = e.message.text;
    if (!message) return;

    this.lastInteractionTs = Date.now();

    const { message: channelMessage } = await this.channel.sendMessage({
      text: '',
      ai_generated: true,
    });

    await this.channel.sendEvent({
      type: 'ai_indicator.update',
      ai_state: 'AI_STATE_THINKING',
      cid: channelMessage.cid,
      message_id: channelMessage.id,
    });

    const handler = new GeminiAIResponseHandler(
      this.genAI,
      this.model,
      this.chatClient,
      this.channel,
      channelMessage,
      message,
    );
    void handler.run();
    this.handlers.push(handler);
  };
}
