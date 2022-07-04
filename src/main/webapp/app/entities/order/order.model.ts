import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IBasket } from 'app/entities/basket/basket.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  createDate?: dayjs.Dayjs;
  status?: OrderStatus;
  user?: IUser | null;
  basket?: IBasket | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs,
    public status?: OrderStatus,
    public user?: IUser | null,
    public basket?: IBasket | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
