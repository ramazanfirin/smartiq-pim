import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IBasketItem } from 'app/entities/basket-item/basket-item.model';
import { BasketStatus } from 'app/entities/enumerations/basket-status.model';

export interface IBasket {
  id?: number;
  createDate?: dayjs.Dayjs;
  status?: BasketStatus;
  totalCost?: number;
  user?: IUser | null;
  basketItems?: IBasketItem[] | null;
}

export class Basket implements IBasket {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs,
    public status?: BasketStatus,
    public totalCost?: number,
    public user?: IUser | null,
    public basketItems?: IBasketItem[] | null
  ) {}
}

export function getBasketIdentifier(basket: IBasket): number | undefined {
  return basket.id;
}
