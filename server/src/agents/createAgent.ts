import { AgentPlatform, AIAgent } from './types';
import { StreamChat } from 'stream-chat';
import { OpenAIAgent } from './openai/OpenAIAgent';
import { apiKey, serverClient } from '../serverClient';

export const createAgent = async (
  user_id: string,
  platform: AgentPlatform = AgentPlatform.OPENAI, 
  channel_type: string,
  channel_id: string,
): Promise<AIAgent> => {
  const client = new StreamChat(apiKey, { allowServerSideConnect: true });
  const token = serverClient.createToken(user_id);
  await client.connectUser({ id: user_id }, token);
  console.log(`User ${user_id} connected successfully.`);

  const channel = client.channel(channel_type, channel_id);
  await channel.watch();

  return new OpenAIAgent(client, channel);
};