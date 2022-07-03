import { IBasket } from 'app/entities/basket/basket.model';
import { IProduct } from 'app/entities/product/product.model';

export interface IBasketItem {
  id?: number;
  quantity?: number;
  totalCost?: number;
  basket?: IBasket | null;
  product?: IProduct | null;
}

export class BasketItem implements IBasketItem {
  constructor(
    public id?: number,
    public quantity?: number,
    public totalCost?: number,
    public basket?: IBasket | null,
    public product?: IProduct | null
  ) {}
}

export function getBasketItemIdentifier(basketItem: IBasketItem): number | undefined {
  return basketItem.id;
}
